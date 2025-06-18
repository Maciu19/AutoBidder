package com.maciu19.autobidder.api.vehicle.dto;

import java.time.Instant;
import java.util.UUID;

public record ManufacturerDto(
        UUID id,
        String name,
        Instant createdDate,
        Instant lastModifiedDate
) {  }
