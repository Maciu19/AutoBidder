package com.maciu19.autobidder.api.auction.service;

import com.maciu19.autobidder.api.auction.dto.AuctionResponseDto;
import com.maciu19.autobidder.api.auction.dto.AuctionSummaryDto;
import com.maciu19.autobidder.api.auction.dto.CreateAuctionRequest;
import com.maciu19.autobidder.api.auction.model.AuctionStatus;
import com.maciu19.autobidder.api.auction.model.FileType;
import com.maciu19.autobidder.api.auction.model.MediaAsset;
import com.maciu19.autobidder.api.auction.repository.MediaAssetRepository;
import com.maciu19.autobidder.api.exception.exceptions.DuplicateResourceException;
import com.maciu19.autobidder.api.exception.exceptions.ForbiddenResourceException;
import com.maciu19.autobidder.api.exception.exceptions.ResourceNotFoundException;
import com.maciu19.autobidder.api.auction.model.Auction;
import com.maciu19.autobidder.api.auction.mapper.AuctionMapper;
import com.maciu19.autobidder.api.shared.filestorage.FileStorageService;
import com.maciu19.autobidder.api.user.model.User;
import com.maciu19.autobidder.api.vehicle.model.VehicleEngineOption;
import com.maciu19.autobidder.api.auction.repository.AuctionRepository;
import com.maciu19.autobidder.api.vehicle.repository.VehicleEngineOptionRepository;

import jakarta.transaction.Transactional;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AuctionServiceImpl implements AuctionService {

    private final VehicleEngineOptionRepository vehicleEngineOptionRepository;
    private final AuctionRepository auctionRepository;
    private final MediaAssetRepository mediaAssetRepository;
    private final FileStorageService fileStorageService;
    private final AuctionMapper auctionMapper;

    public AuctionServiceImpl(
            VehicleEngineOptionRepository vehicleEngineOptionRepository,
            AuctionRepository auctionRepository,
            MediaAssetRepository mediaAssetRepository,
            FileStorageService fileStorageService,
            AuctionMapper auctionMapper) {
        this.vehicleEngineOptionRepository = vehicleEngineOptionRepository;
        this.auctionRepository = auctionRepository;
        this.mediaAssetRepository = mediaAssetRepository;
        this.fileStorageService = fileStorageService;
        this.auctionMapper = auctionMapper;
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

        Auction auction = new Auction();

        auction.setSeller(seller);
        auction.setVehicleEngineOption(vehicle);
        auction.setStatus(AuctionStatus.PENDING);
        auction.setVin(request.vin());
        auction.setLocation(request.location());
        auction.setStartingPrice(request.startingPrice());
        auction.setSteeringWheelSide(request.steeringWheelside());
        auction.setHasWarranty(request.hasWarranty());
        auction.setNoCrashRegistered(request.noCrashRegistered());
        auction.setMakeYear(request.makeYear());
        auction.setMileage(request.mileage());
        auction.setExteriorColor(request.exteriorColor());
        auction.setInteriorColor(request.interiorColor());
        auction.setDescription(request.description());
        auction.setModifications(request.modifications());
        auction.setKnownFlaws(request.knownFlaws());
        auction.setRecentServiceHistory(request.recentServiceHistory());
        auction.setOtherItemsIncluded(request.otherItemsIncluded());
        auction.setOwnershipHistory(request.ownershipHistory());
        auction.setFeatures(request.features());

        Auction savedAuction = auctionRepository.save(auction);

        Hibernate.initialize(auction.getBids());
        Hibernate.initialize(auction.getMediaAssets());

        return auctionMapper.toDto(savedAuction);
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
}
