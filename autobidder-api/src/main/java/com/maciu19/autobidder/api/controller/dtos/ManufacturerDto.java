package com.maciu19.autobidder.api.controller.dtos;

import com.maciu19.autobidder.api.model.Manufacturer;

import java.time.Instant;
import java.util.UUID;

public record ManufacturerDto(
        UUID id,
        String name,
        Instant createdDate,
        Instant lastModifiedDate
) {
    public static ManufacturerDto mapToDto(Manufacturer manufacturer) {
        return new ManufacturerDto(
                manufacturer.getId(),
                manufacturer.getName(),
                manufacturer.getCreatedDate(),
                manufacturer.getLastModifiedDate()
        );
    }
}
