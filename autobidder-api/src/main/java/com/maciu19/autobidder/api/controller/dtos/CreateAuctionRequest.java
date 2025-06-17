package com.maciu19.autobidder.api.controller.dtos;

import com.maciu19.autobidder.api.model.enums.Feature;
import com.maciu19.autobidder.api.model.enums.SteeringWheelSide;
import jakarta.validation.constraints.*;

import java.time.Year;
import java.util.Set;
import java.util.UUID;

public record CreateAuctionRequest (

    @NotNull(message = "Vehicle engine option ID cannot be null.")
    UUID vehicleEngineOptionId,

    @NotBlank(message = "VIN cannot be blank.")
    @Size(min = 17, max = 17, message = "VIN must be exactly 17 characters long.")
    String vin,

    @NotBlank(message = "Location cannot be blank.")
    String location,

    @NotNull(message = "Starting price cannot be null.")
    @Positive(message = "Starting price must be a positive number.")
    Double startingPrice,

    @NotNull(message = "Duration must be specified.")
    @Min(value = 1, message = "Duration must be at least 1 day.")
    @Max(value = 14, message = "Duration cannot be more than 14 days.")
    Integer durationInDays,

    @NotNull(message = "Steering wheel side must be specified.")
    SteeringWheelSide steeringWheelside,

    boolean hasWarranty,
    boolean noCrashRegistered,

    @NotNull(message = "Make year cannot be null.")
    @PastOrPresent(message = "Make year cannot be in the future.")
    Year makeYear,

    @PositiveOrZero(message = "Mileage must be zero or a positive number.")
    int mileage,

    @NotBlank(message = "Exterior color cannot be blank.")
    String exteriorColor,

    @NotBlank(message = "Exterior color cannot be blank.")
    String interiorColor,

    @NotBlank(message = "Description cannot be blank.")
    @Size(min = 50, message = "Description must be at least 50 characters long.")
    String description,

    String modifications,
    String knownFlaws,
    String recentServiceHistory,
    String otherItemsIncluded,
    String ownershipHistory,

    @NotEmpty(message = "At least one feature must be selected.")
    Set<Feature> features
) { }
