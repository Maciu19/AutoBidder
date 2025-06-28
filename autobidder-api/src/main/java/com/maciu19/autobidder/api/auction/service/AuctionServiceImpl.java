package com.maciu19.autobidder.api.auction.service;

import com.maciu19.autobidder.api.auction.dto.*;
import com.maciu19.autobidder.api.auction.model.*;
import com.maciu19.autobidder.api.auction.repository.MediaAssetRepository;
import com.maciu19.autobidder.api.exception.exceptions.DuplicateResourceException;
import com.maciu19.autobidder.api.exception.exceptions.ForbiddenResourceException;
import com.maciu19.autobidder.api.exception.exceptions.ResourceConflictException;
import com.maciu19.autobidder.api.exception.exceptions.ResourceNotFoundException;
import com.maciu19.autobidder.api.auction.mapper.AuctionMapper;
import com.maciu19.autobidder.api.notification.model.NotificationType;
import com.maciu19.autobidder.api.notification.service.NotificationService;
import com.maciu19.autobidder.api.shared.filestorage.FileStorageService;
import com.maciu19.autobidder.api.user.mapper.UserMapper;
import com.maciu19.autobidder.api.user.model.User;
import com.maciu19.autobidder.api.vehicle.dto.VehicleInfo;
import com.maciu19.autobidder.api.vehicle.model.VehicleEngineOption;
import com.maciu19.autobidder.api.auction.repository.AuctionRepository;
import com.maciu19.autobidder.api.vehicle.repository.VehicleEngineOptionRepository;

import com.maciu19.autobidder.api.vehicle.service.VehicleService;
import jakarta.transaction.Transactional;

import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuctionServiceImpl implements AuctionService {

    private static final int MINIMUM_DATA_POINTS = 5;
    private final static Logger log = LoggerFactory.getLogger(AuctionServiceImpl.class);

    private final VehicleService vehicleService;
    private final VehicleEngineOptionRepository vehicleEngineOptionRepository;
    private final AuctionRepository auctionRepository;
    private final MediaAssetRepository mediaAssetRepository;
    private final FileStorageService fileStorageService;
    private final AuctionMapper auctionMapper;
    private final UserMapper userMapper;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final NotificationService notificationService;

    public AuctionServiceImpl(
            VehicleService vehicleService,
            VehicleEngineOptionRepository vehicleEngineOptionRepository,
            AuctionRepository auctionRepository,
            MediaAssetRepository mediaAssetRepository,
            FileStorageService fileStorageService,
            AuctionMapper auctionMapper,
            UserMapper userMapper,
            SimpMessagingTemplate simpMessagingTemplate,
            NotificationService notificationService) {
        this.vehicleService = vehicleService;
        this.vehicleEngineOptionRepository = vehicleEngineOptionRepository;
        this.auctionRepository = auctionRepository;
        this.mediaAssetRepository = mediaAssetRepository;
        this.fileStorageService = fileStorageService;
        this.auctionMapper = auctionMapper;
        this.userMapper = userMapper;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.notificationService = notificationService;
    }

    @Override
    public List<AuctionSummaryDto> getUserAuctions(User currentUser) {
        List<Auction> auctions = auctionRepository.findAuctionByUser(currentUser);

        return auctionMapper.toListSummaryDto(auctions);
    }

    @Override
    public List<AuctionSummaryDto> getActiveAuctions() {
        List<Auction> activeAuctions = auctionRepository.findActiveAuctionsForList(AuctionStatus.ACTIVE);

        return auctionMapper.toListSummaryDto(activeAuctions);
    }

    @Override
    public AuctionResponseDto getAuctionById(UUID auctionId) {
        Auction auction = auctionRepository.findByIdWithDetails(auctionId)
                .orElseThrow(() -> new ResourceNotFoundException("Auction not found with id: " + auctionId));

        return auctionMapper.toDto(auction);
    }

    @Override
    @Transactional()
    public AuctionResponseDto createAuction(CreateAuctionRequest request, User seller) {
        if (auctionRepository.findByVin(request.vin()).isPresent()) {
            throw new DuplicateResourceException("An auction with VIN '" + request.vin() + "' already exists.");
        }

        VehicleEngineOption vehicle = vehicleEngineOptionRepository
                .findByIdWithDetails(request.vehicleEngineOptionId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + request.vehicleEngineOptionId()));

        Integer startYear = vehicle.getVehicleModelGeneration().getStartYear();
        Integer endYear = vehicle.getVehicleModelGeneration().getEndYear();

        if (startYear >= request.makeYear().getValue() || (endYear != null && endYear <= request.makeYear().getValue())) {
            throw new ResourceConflictException("Make year " + request.makeYear().getValue() + " should be between vehicle model generation years " + startYear + " - " + endYear);
        }

        Auction auction = Auction.builder()
                .seller(seller)
                .vehicleEngineOption(vehicle)
                .status(AuctionStatus.PENDING)
                .vin(request.vin())
                .title(request.title())
                .location(request.location())
                .startingPrice(request.startingPrice())
                .currentPrice(request.startingPrice()) // initialize with startingPrice
                .steeringWheelSide(request.steeringWheelside())
                .hasWarranty(request.hasWarranty())
                .noCrashRegistered(request.noCrashRegistered())
                .makeYear(request.makeYear())
                .mileage(request.mileage())
                .exteriorColor(request.exteriorColor())
                .interiorColor(request.interiorColor())
                .description(request.description())
                .modifications(request.modifications())
                .knownFlaws(request.knownFlaws())
                .recentServiceHistory(request.recentServiceHistory())
                .otherItemsIncluded(request.otherItemsIncluded())
                .ownershipHistory(request.ownershipHistory())
                .features(request.features())
                .build();

        Auction savedAuction = auctionRepository.save(auction);

        Hibernate.initialize(auction.getBids());
        Hibernate.initialize(auction.getMediaAssets());

        return auctionMapper.toDto(savedAuction);
    }

    @Override
    @Transactional
    public void updateAuction(AuctionUpdateDto dto, User currentUser) {
        Auction auction = auctionRepository.findById(dto.auctionId())
                .orElseThrow(() -> new ResourceNotFoundException("Auction not found with id: " + dto.auctionId()));

        if (!auction.getSeller().getId().equals(currentUser.getId())) {
            throw new ForbiddenResourceException("You do not have permission to modify this auction.");
        }

        boolean hasBids = auction.getBids() != null && !auction.getBids().isEmpty();
        if (hasBids) {
            throw new IllegalStateException("Cannot update auction details after a bid has been placed.");
        }

        AuctionStatus status = auction.getStatus();
        if (!(status == AuctionStatus.PENDING || status == AuctionStatus.SCHEDULED)) {
            throw new IllegalStateException("Auction can only be updated when in PENDING or SCHEDULED status.");
        }

        if (dto.title() != null) {
            auction.setTitle(dto.title());
        }
        if (dto.description() != null) {
            auction.setDescription(dto.description());
        }
        if (dto.location() != null) {
            auction.setLocation(dto.location());
        }
        if (dto.modifications() != null) {
            auction.setModifications(dto.modifications());
        }
        if (dto.knownFlaws() != null) {
            auction.setKnownFlaws(dto.knownFlaws());
        }
        if (dto.recentServiceHistory() != null) {
            auction.setRecentServiceHistory(dto.recentServiceHistory());
        }
        if (dto.otherItemsIncluded() != null) {
            auction.setOtherItemsIncluded(dto.otherItemsIncluded());
        }
        if (dto.ownershipHistory() != null) {
            auction.setOwnershipHistory(dto.ownershipHistory());
        }
        if (dto.features() != null) {
            auction.setFeatures(dto.features());
        }
        if (dto.endTime() != null) {
            if (dto.endTime().isBefore(auction.getEndTime())) {
                throw new IllegalArgumentException("Auction end time can only be extended, not shortened.");
            }
            if (dto.endTime().isBefore(auction.getStartTime())) {
                throw new IllegalArgumentException("Auction end time must be after the start time.");
            }

            auction.setEndTime(dto.endTime());
        }

        auctionRepository.save(auction);
    }

    @Override
    @Transactional
    public void publishAction(AuctionPublishDto dto, User currentUser) {
        Auction auction = auctionRepository.findById(dto.auctionId())
                .orElseThrow(() -> new ResourceNotFoundException("Auction not found with id: " + dto.auctionId()));

        if (!auction.getSeller().getId().equals(currentUser.getId())) {
            throw new ForbiddenResourceException("You do not have permission to modify this auction.");
        }

        if (auction.getStatus() != AuctionStatus.PENDING) {
            throw new IllegalStateException("Only auctions in PENDING status can be published. Current status: " + auction.getStatus());
        }

        if (dto.startTime() == null || dto.endTime() == null) {
            throw new IllegalArgumentException("Start and end times must be provided to publish an auction.");
        }

        if (dto.startTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Auction start time cannot be in the past.");
        }

        if (auction.getMediaAssets() == null || auction.getMediaAssets().isEmpty()) {
            throw new IllegalStateException("Cannot publish an auction without at least one photo or video.");
        }

        auction.setStartTime(dto.startTime());
        auction.setEndTime(dto.endTime());
        auction.setStatus(AuctionStatus.SCHEDULED);
        auctionRepository.save(auction);

        sendWebSocketNotification(auction);
    }

    @Override
    @Transactional
    public void cancelAuction(UUID id, User currentUser) {
        Auction auction = auctionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Auction not found with id: " + id));

        if (!auction.getSeller().getId().equals(currentUser.getId())) {
            throw new ForbiddenResourceException("You do not have permission to modify this auction.");
        }

        if (auction.getBids() != null && !auction.getBids().isEmpty()) {
            throw new IllegalStateException("Cannot cancel an auction that has already received bids.");
        }

        AuctionStatus currentStatus = auction.getStatus();
        if (!(currentStatus == AuctionStatus.PENDING || currentStatus == AuctionStatus.SCHEDULED)) {
            throw new IllegalStateException("An auction in the '" + currentStatus + "' state cannot be canceled.");
        }

        auction.setStatus(AuctionStatus.CANCELLED);
        auctionRepository.save(auction);

        sendWebSocketNotification(auction);
    }

    @Override
    @Transactional
    public MediaAsset addMediaToAuction(
            UUID auctionId, User currentUser,
            MultipartFile file, String title,
            FileType fileType, Integer displayOrder) throws IOException {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new ResourceNotFoundException("Auction not found with id: " + auctionId));

        if (!auction.getSeller().getId().equals(currentUser.getId())) {
            throw new ForbiddenResourceException("You do not have permission to add media to this auction.");
        }

        String fileUrl = fileStorageService.uploadFile(file, auctionId.toString());

        MediaAsset newAsset = new MediaAsset();
        newAsset.setAuction(auction);
        newAsset.setFileUrl(fileUrl);
        newAsset.setTitle(title);
        newAsset.setFileType(fileType);

        if (displayOrder == null) {
            newAsset.setDisplayOrder(auction.getMediaAssets().size());
        } else {
            newAsset.setDisplayOrder(displayOrder);
        }

        auction.addMediaAsset(newAsset);

        Auction savedAuction = auctionRepository.save(auction);

        int lastIndex = savedAuction.getMediaAssets().size() - 1;
        if (lastIndex < 0) {
            throw new IllegalStateException("Could not find the newly saved media asset.");
        }

        return savedAuction.getMediaAssets().get(lastIndex);
    }

    @Override
    @Transactional
    public void removeMediaAssetFromAuction(UUID auctionId, Long mediaAssetId, User currentUser) throws IOException {
        MediaAsset asset = mediaAssetRepository.findById(mediaAssetId)
                .orElseThrow(() -> new ResourceNotFoundException("Media asset not found with id: " + mediaAssetId));

        if (!asset.getAuction().getId().equals(auctionId)) {
            throw new ForbiddenResourceException("Media asset does not belong to the specified auction.");
        }

        if (!asset.getAuction().getSeller().getId().equals(currentUser.getId())) {
            throw new ForbiddenResourceException("You do not have permission to delete this media asset.");
        }

        fileStorageService.deleteFile(asset.getFileUrl());

        mediaAssetRepository.delete(asset);
    }

    @Override
    public PriceRecommendationDto getPriceRecommendationForVehicle(UUID engineId) {
        Optional<VehicleInfo> vehicleInfo = vehicleService.getVehicleInfo(engineId);

        if (vehicleInfo.isEmpty()) {
            throw new ResourceNotFoundException("Cannot find engine with id: " + engineId);
        }

        // --- STAGE 1: Broad search for Manufacturer + Model ---
        List<AuctionDataPoint> currentData = auctionRepository.findDataPointsByManufacturerAndModel(
                vehicleInfo.get().manufacturerId(),
                vehicleInfo.get().vehicleModelId());

        AnalysisLevel currentLevel = AnalysisLevel.MODEL;
        if (currentData.size() < MINIMUM_DATA_POINTS) {
            return PriceRecommendationDto.notEnoughData();
        }

        // --- STAGE 2: Attempt to refine by Generation ---
        List<AuctionDataPoint> generationFilteredData = currentData.stream()
                .filter(dp -> vehicleInfo.get().vehicleModelGenerationId().equals(dp.generationId()))
                .toList();

        if (!generationFilteredData.isEmpty() && (double) generationFilteredData.size() / currentData.size() > 0.50) {
            currentData = generationFilteredData;
            currentLevel = AnalysisLevel.GENERATION;
        }

        // --- STAGE 3: Attempt to refine by Engine ---
        List<AuctionDataPoint> engineFilteredData = currentData.stream()
                .filter(dp -> engineId.equals(dp.engineId()))
                .toList();

        if (!engineFilteredData.isEmpty() && (double) engineFilteredData.size() / currentData.size() > 0.25) {
            currentData = engineFilteredData;
            currentLevel = AnalysisLevel.ENGINE;
        }

        // --- FINAL CALCULATION ---
        List<BigDecimal> finalPrices = currentData.stream().map(AuctionDataPoint::price).toList();

        // Check if our final dataset is still large enough
        if (finalPrices.size() < MINIMUM_DATA_POINTS / 2) {
            return PriceRecommendationDto.notEnoughData();
        }

        BigDecimal sum = finalPrices.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal average = sum.divide(new BigDecimal(finalPrices.size()), RoundingMode.HALF_UP);
        BigDecimal min = finalPrices.stream().min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        BigDecimal max = finalPrices.stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        BigDecimal standardDeviation = calculateStandardDeviation(finalPrices, average);

        // --- APPLY THE THRESHOLD/CONFIDENCE ---
        // The "rangeMultiplier" from our enum adjusts the width of the fair market range.
        // 1.0 is widest (Model-level), 0.5 is tightest (Engine-level).
        BigDecimal adjustedHalfStdDev = standardDeviation
                .multiply(BigDecimal.valueOf(currentLevel.rangeMultiplier))
                .divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP);

        BigDecimal rangeStart = average.subtract(adjustedHalfStdDev);
        BigDecimal rangeEnd = average.add(adjustedHalfStdDev);

        return new PriceRecommendationDto(
                finalPrices.size(),
                currentLevel.displayName,
                currentLevel.confidenceScore,
                average.setScale(0, RoundingMode.HALF_UP),
                min.setScale(0, RoundingMode.HALF_UP),
                max.setScale(0, RoundingMode.HALF_UP),
                average.setScale(0, RoundingMode.HALF_UP),
                rangeStart.setScale(0, RoundingMode.HALF_UP),
                rangeEnd.setScale(0, RoundingMode.HALF_UP)
        );
    }

    @Override
    public List<AuctionSummaryDto> findSimilarActiveAuctions(UUID auctionId) {
        Optional<Auction> currentAuctionOpt = auctionRepository.findById(auctionId);

        if (currentAuctionOpt.isEmpty() || currentAuctionOpt.get().getCurrentPrice() == null) {
            return Collections.emptyList();
        }
        Auction currentAuction = currentAuctionOpt.get();

        BigDecimal priceMargin = new BigDecimal("0.25");
        BigDecimal currentPrice = currentAuction.getCurrentPrice();
        BigDecimal minPrice = currentPrice.subtract(currentPrice.multiply(priceMargin));
        BigDecimal maxPrice = currentPrice.add(currentPrice.multiply(priceMargin));

        var vehicleModel = currentAuction.getVehicleEngineOption()
                .getVehicleModelGeneration()
                .getVehicleModel();

        UUID manufacturerId = vehicleModel.getManufacturer().getId();
        UUID modelId = vehicleModel.getId();

        List<Auction> similarAuctions = auctionRepository.findSimilarActiveAuctions(
                manufacturerId,
                modelId,
                currentAuction.getId(),
                minPrice,
                maxPrice
        );

        return auctionMapper.toListSummaryDto(similarAuctions);
    }

    private BigDecimal calculateStandardDeviation(List<BigDecimal> prices, BigDecimal average) {
        if (prices.size() < 2) return BigDecimal.ZERO;
        BigDecimal sumOfSquares = prices.stream()
                .map(price -> price.subtract(average).pow(2))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal variance = sumOfSquares.divide(new BigDecimal(prices.size() - 1), RoundingMode.HALF_UP);
        return BigDecimal.valueOf(Math.sqrt(variance.doubleValue()));
    }

    @Transactional
    @Scheduled(fixedRate = 30000)
    public void manageAuctionLifecycle() {
        startScheduledAuctions();
        closeExpiredAuctions();
    }

    private void startScheduledAuctions() {
        List<Auction> auctionsToStart = auctionRepository
                .findAllByStatusAndStartTimeBeforeForList(AuctionStatus.SCHEDULED, LocalDateTime.now());

        if (auctionsToStart.isEmpty()) {
            return;
        }

        for (Auction auction : auctionsToStart) {
            log.info("Auction id='{}' title='{}' is starting now.", auction.getId(), auction.getTitle());
            auction.setStatus(AuctionStatus.ACTIVE);
        }

        auctionRepository.saveAll(auctionsToStart);

        for (Auction auction : auctionsToStart) {
            sendWebSocketNotification(auction);
        }
    }

    private void closeExpiredAuctions() {
        List<Auction> expiredAuctions = auctionRepository
                .findAllByStatusAndEndTimeBeforeForList(AuctionStatus.ACTIVE, LocalDateTime.now());

        if (expiredAuctions.isEmpty()) {
            return;
        }

        for (Auction auction : expiredAuctions) {
            log.info("Auction id='{}' title='{}' has expired. Closing now.", auction.getId(), auction.getTitle());
            auction.setStatus(AuctionStatus.ENDED);
        }

        auctionRepository.saveAll(expiredAuctions);

        for (Auction auction : expiredAuctions) {
            sendWebSocketNotification(auction);
        }
    }

    private void sendWebSocketNotification(Auction auction) {
        try {
            simpMessagingTemplate.convertAndSend("/topic/auctions/statusUpdate", auctionMapper.toSummaryDto(auction));

            notificationService.createGlobalNotification(
                    "A new auction was created with title " + auction.getTitle(),
                    NotificationType.AUCTION_CREATED);

        } catch (Exception e) {
            log.error("Failed to send WebSocket notification for auction {}: {}",
                    auction.getId(), e.getMessage(), e);
        }
    }
}
