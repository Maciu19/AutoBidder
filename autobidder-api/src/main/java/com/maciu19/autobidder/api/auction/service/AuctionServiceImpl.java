package com.maciu19.autobidder.api.auction.service;

import com.maciu19.autobidder.api.auction.dto.AuctionResponseDto;
import com.maciu19.autobidder.api.auction.dto.CreateAuctionRequest;
import com.maciu19.autobidder.api.exception.exceptions.DuplicateResourceException;
import com.maciu19.autobidder.api.exception.exceptions.ResourceNotFoundException;
import com.maciu19.autobidder.api.auction.model.Auction;
import com.maciu19.autobidder.api.auction.mapper.AuctionMapper;
import com.maciu19.autobidder.api.user.model.User;
import com.maciu19.autobidder.api.vehicle.model.VehicleEngineOption;
import com.maciu19.autobidder.api.auction.repository.AuctionRepository;
import com.maciu19.autobidder.api.vehicle.repository.VehicleEngineOptionRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuctionServiceImpl implements AuctionService {

    private final VehicleEngineOptionRepository vehicleEngineOptionRepository;
    private final AuctionRepository auctionRepository;
    private final AuctionMapper auctionMapper;

    public AuctionServiceImpl(
            VehicleEngineOptionRepository vehicleEngineOptionRepository,
            AuctionRepository auctionRepository,
            AuctionMapper auctionMapper) {
        this.vehicleEngineOptionRepository = vehicleEngineOptionRepository;
        this.auctionRepository = auctionRepository;
        this.auctionMapper = auctionMapper;
    }

    @Override
    @Transactional
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
        auction.setVin(request.vin());
        auction.setLocation(request.location());
        auction.setStartingPrice(request.startingPrice());
        auction.setEndTime(LocalDateTime.now().plusDays(request.durationInDays()));
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

        return auctionMapper.toDto(savedAuction);
    }
}
