package com.maciu19.autobidder.api.controller.dtos;

import com.maciu19.autobidder.api.model.VehicleModel;
import com.maciu19.autobidder.api.model.enums.VehicleModelSegment;

import java.time.Instant;
import java.util.UUID;

public record VehicleModelDto(
        UUID id,
        String name,
        VehicleModelSegment vehicleModelSegment,
        Instant createdDate,
        Instant lastModifiedDate
) {
    public static VehicleModelDto toDto(VehicleModel vehicleModel) {
        return new VehicleModelDto(
                vehicleModel.getId(),
                vehicleModel.getName(),
                vehicleModel.getVehicleModelSegment(),
                vehicleModel.getCreatedDate(),
                vehicleModel.getLastModifiedDate()
        );
    }
}
