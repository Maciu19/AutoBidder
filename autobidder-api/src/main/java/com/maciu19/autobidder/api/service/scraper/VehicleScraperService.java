package com.maciu19.autobidder.api.service.scraper;

import com.maciu19.autobidder.api.model.Manufacturer;
import com.maciu19.autobidder.api.model.VehicleModel;
import com.maciu19.autobidder.api.model.VehicleModelGeneration;

import java.util.List;
import java.util.UUID;

public interface VehicleScraperService {

    List<Manufacturer> getAllManufacturers();

    List<VehicleModel> getAllVehicleModelsForManufacturer(UUID manufacturerId);

    List<VehicleModelGeneration> getAllModelGenerationForVehicleModel (UUID vehicleModelId);
}
