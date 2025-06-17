package com.maciu19.autobidder.api.vehicle.dto;

import com.maciu19.autobidder.api.vehicle.model.Manufacturer;

import java.time.Instant;
import java.util.UUID;

public record ManufacturerDto(
        UUID id,
        String name,
        Instant createdDate,
        Instant lastModifiedDate
) {
    public static ManufacturerDto toDto(Manufacturer manufacturer) {
        return new ManufacturerDto(
                manufacturer.getId(),
                manufacturer.getName(),
                manufacturer.getCreatedDate(),
                manufacturer.getLastModifiedDate()
        );
    }
}
