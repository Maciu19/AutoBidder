package com.maciu19.autobidder.api.auction.dto;

import com.maciu19.autobidder.api.auction.model.AuctionStatus;
import com.maciu19.autobidder.api.bid.dto.BidDto;
import com.maciu19.autobidder.api.user.dto.UserDto;
import com.maciu19.autobidder.api.vehicle.dto.VehicleInfo;
import com.maciu19.autobidder.api.auction.model.Feature;
import com.maciu19.autobidder.api.auction.model.SteeringWheelSide;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record AuctionResponseDto (
    UUID id,
    UserDto seller,
    VehicleInfo vehicleInfo,
    String vin,
    String location,
    BigDecimal startingPrice,
    LocalDateTime startTime,
    LocalDateTime endTime,
    AuctionStatus status,
    BigDecimal currentPrice,
    UserDto winningUser,
    List<BidDto> bids,
    SteeringWheelSide steeringWheelSide,
    boolean hasWarranty,
    boolean noCrashRegistered,
    Year makeYear,
    int mileage,
    String exteriorColor,
    String interiorColor,
    List<MediaAssetDto> mediaAssets,
    Set<Feature> features,
    String title,
    String description,
    String modifications,
    String knownFlaws,
    String recentServiceHistory,
    String otherItemsIncluded,
    String ownershipHistory,
    Instant createdDate,
    Instant lastModifiedDate
) { }