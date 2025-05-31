package com.maciu19.autobidder.api.service.scraper;

import com.maciu19.autobidder.api.model.Manufacturer;
import com.maciu19.autobidder.api.model.VehicleModel;

import java.util.List;
import java.util.UUID;

public interface VehicleScraperService {

    List<Manufacturer> getAllManufacturers();

    List<VehicleModel> getAllVehicleModelsForManufacturer(UUID manufacturerId);
}
