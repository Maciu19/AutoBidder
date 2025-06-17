package com.maciu19.autobidder.api.vehicle.service;

import com.maciu19.autobidder.api.vehicle.model.Manufacturer;
import com.maciu19.autobidder.api.vehicle.model.VehicleEngineOption;
import com.maciu19.autobidder.api.vehicle.model.VehicleModel;
import com.maciu19.autobidder.api.vehicle.model.VehicleModelGeneration;
import com.maciu19.autobidder.api.vehicle.dto.VehicleInfo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VehicleService {

    List<Manufacturer> getAllManufacturers();

    List<VehicleModel> getAllVehicleModelForManufacturer(UUID manufacturerId);

    List<VehicleModelGeneration> getAllModelGenerationForVehicleModel(UUID vehicleModelId);

    List<VehicleEngineOption> getAllEngineOptionsForModelGeneration(UUID vehicleModelGenerationId);

    Optional<VehicleInfo> getVehicleInfo(UUID vehicleEngineOptionId);
}
