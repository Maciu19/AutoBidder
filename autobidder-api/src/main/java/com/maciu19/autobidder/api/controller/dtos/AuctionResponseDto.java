package com.maciu19.autobidder.api.controller.dtos;

import com.maciu19.autobidder.api.model.User;
import com.maciu19.autobidder.api.model.dtos.VehicleInfo;
import com.maciu19.autobidder.api.model.enums.SteeringWheelSide;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Year;
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