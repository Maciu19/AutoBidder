package com.maciu19.autobidder.api.service;

import com.maciu19.autobidder.api.model.Manufacturer;
import com.maciu19.autobidder.api.model.VehicleModel;
import com.maciu19.autobidder.api.model.VehicleModelGeneration;

import java.util.List;
import java.util.UUID;

public interface VehicleService {

    List<Manufacturer> getAllManufacturers();

    List<VehicleModel> getAllVehicleModelForManufacturer(UUID manufacturerId);

    List<VehicleModelGeneration> getAllModelGenerationForVehicleModel(UUID vehicleModelId);
}
