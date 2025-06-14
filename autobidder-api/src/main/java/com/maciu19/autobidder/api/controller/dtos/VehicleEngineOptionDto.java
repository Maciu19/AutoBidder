package com.maciu19.autobidder.api.controller.dtos;

import com.maciu19.autobidder.api.model.VehicleEngineOption;
import com.maciu19.autobidder.api.model.enums.FuelType;

import java.time.Instant;
import java.util.UUID;

public record VehicleEngineOptionDto(
        UUID id,
        String url,
        String name,
        FuelType fuelType,
        Instant createdDate,
        Instant lastModifiedDate
) {
    public static VehicleEngineOptionDto toDto(VehicleEngineOption engineOption) {
        return new VehicleEngineOptionDto(
                engineOption.getId(),
                engineOption.getUrl(),
                engineOption.getName(),
                engineOption.getFuelType(),
                engineOption.getCreatedDate(),
                engineOption.getLastModifiedDate()
        );
    }
}
