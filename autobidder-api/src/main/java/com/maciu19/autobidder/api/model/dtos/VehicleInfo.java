package com.maciu19.autobidder.api.model.dtos;

import com.maciu19.autobidder.api.model.enums.FuelType;
import com.maciu19.autobidder.api.model.enums.VehicleModelSegment;

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
