package com.maciu19.autobidder.api.service;

import com.maciu19.autobidder.api.model.Manufacturer;
import com.maciu19.autobidder.api.repository.ManufacturerRepository;
import com.maciu19.autobidder.api.service.scraper.VehicleScraperService;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ManufacturerServiceImpl implements ManufacturerService {

    private final ManufacturerRepository manufacturerRepository;
    private final VehicleScraperService scraperService;

    public ManufacturerServiceImpl(
            ManufacturerRepository manufacturerRepository,
            VehicleScraperService scraperService) {
        this.manufacturerRepository = manufacturerRepository;
        this.scraperService = scraperService;
    }

    @Override
    public List<Manufacturer> getAllOrCreateManufacturers() {
        List<Manufacturer> manufacturers = manufacturerRepository.findAll();

        if (!manufacturers.isEmpty()) {
            return manufacturers;
        }

        manufacturers = scraperService.getAllManufacturers();

        if (!manufacturers.isEmpty()) {
            return manufacturerRepository.saveAll(manufacturers);
        }

        return Collections.emptyList();
    }
}
