package com.maciu19.autobidder.api.vehicle.dto;

import com.maciu19.autobidder.api.vehicle.model.FuelType;

import java.time.Instant;
import java.util.UUID;

public record VehicleEngineOptionDto(
        UUID id,
        String url,
        String name,
        FuelType fuelType,
        Instant createdDate,
        Instant lastModifiedDate
) { }
