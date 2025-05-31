package com.maciu19.autobidder.api.service;

import com.maciu19.autobidder.api.model.Manufacturer;
import com.maciu19.autobidder.api.model.VehicleModel;

import java.util.List;
import java.util.UUID;

public interface ManufacturerService {

    List<Manufacturer> getAllOrCreateManufacturers();

    List<VehicleModel> getAllOrCreateVehicleModelForManufacturer(UUID manufacturerId);
}
