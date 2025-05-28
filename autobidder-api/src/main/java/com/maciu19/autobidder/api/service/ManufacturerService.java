package com.maciu19.autobidder.api.service;

import com.maciu19.autobidder.api.model.Manufacturer;

import java.util.List;

public interface ManufacturerService {

    List<Manufacturer> getAllOrCreateManufacturers();

}
