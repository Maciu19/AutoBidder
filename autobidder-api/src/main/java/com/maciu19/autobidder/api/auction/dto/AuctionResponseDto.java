package com.maciu19.autobidder.api.auction.dto;

import com.maciu19.autobidder.api.user.model.User;
import com.maciu19.autobidder.api.vehicle.dto.VehicleInfo;
import com.maciu19.autobidder.api.auction.model.Feature;
import com.maciu19.autobidder.api.auction.model.SteeringWheelSide;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record AuctionResponseDto (
    UUID id,
    User seller,
    VehicleInfo vehicleInfo,
    String vin,
    String location,
    Double startingPrice,
    LocalDateTime endTime,
    SteeringWheelSide steeringWheelSide,
    boolean hasWarranty,
    boolean noCrashRegistered,
    Year makeYear,
    int mileage,
    Set<Feature> features,
    List<MediaAssetDto> mediaAssets,
    String exteriorColor,
    String interiorColor,
    String description,
    String modifications,
    String knownFlaws,
    String recentServiceHistory,
    String otherItemsIncluded,
    String ownershipHistory,
    Instant createdDate,
    Instant lastModifiedDate
) { }