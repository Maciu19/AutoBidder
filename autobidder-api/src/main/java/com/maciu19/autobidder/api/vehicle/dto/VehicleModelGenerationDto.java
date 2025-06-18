package com.maciu19.autobidder.api.vehicle.dto;

import java.time.Instant;
import java.util.UUID;

public record VehicleModelGenerationDto(
        UUID id,
        Integer startYear,
        Integer endYear,
        Instant createdDate,
        Instant lastModifiedDate
) { }
