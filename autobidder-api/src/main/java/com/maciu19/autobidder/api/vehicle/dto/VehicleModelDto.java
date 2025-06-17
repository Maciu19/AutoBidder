package com.maciu19.autobidder.api.vehicle.dto;

import com.maciu19.autobidder.api.vehicle.model.VehicleModel;
import com.maciu19.autobidder.api.vehicle.model.VehicleModelSegment;

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
