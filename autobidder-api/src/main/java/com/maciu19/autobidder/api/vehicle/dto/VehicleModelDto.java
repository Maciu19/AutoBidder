package com.maciu19.autobidder.api.vehicle.dto;

import com.maciu19.autobidder.api.vehicle.model.VehicleModelSegment;

import java.time.Instant;
import java.util.UUID;

public record VehicleModelDto(
        UUID id,
        String name,
        VehicleModelSegment vehicleModelSegment,
        Instant createdDate,
        Instant lastModifiedDate
) { }
