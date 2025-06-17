package com.maciu19.autobidder.api.vehicle.dto;

import com.maciu19.autobidder.api.vehicle.model.FuelType;
import com.maciu19.autobidder.api.vehicle.model.VehicleModelSegment;

import java.util.UUID;

public record VehicleInfo(
        UUID manufacturerId,
        UUID vehicleModelId,
        UUID vehicleModelGenerationId,
        UUID vehicleEngineOptionId,
        String manufacturerName,
        String modelName,
        VehicleModelSegment modelSegment,
        String modelGenerationName,
        Integer modelGenerationStartYear,
        Integer modelGenerationEndYear,
        String engineName,
        FuelType fuelType
) { }
