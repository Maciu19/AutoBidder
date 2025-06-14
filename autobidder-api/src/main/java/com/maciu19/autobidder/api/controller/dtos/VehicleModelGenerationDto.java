package com.maciu19.autobidder.api.controller.dtos;

import com.maciu19.autobidder.api.model.VehicleModelGeneration;

import java.time.Instant;
import java.util.UUID;

public record VehicleModelGenerationDto(
        UUID id,
        Integer startYear,
        Integer endYear,
        Instant createdDate,
        Instant lastModifiedDate
) {
    public static VehicleModelGenerationDto toDto(VehicleModelGeneration vehicleModelGeneration) {
        return new VehicleModelGenerationDto(
                vehicleModelGeneration.getId(),
                vehicleModelGeneration.getStartYear(),
                vehicleModelGeneration.getEndYear(),
                vehicleModelGeneration.getCreatedDate(),
                vehicleModelGeneration.getLastModifiedDate()
        );
    }
}
