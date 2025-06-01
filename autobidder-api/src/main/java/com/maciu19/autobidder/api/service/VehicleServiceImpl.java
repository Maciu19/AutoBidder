package com.maciu19.autobidder.api.service;

import com.maciu19.autobidder.api.model.Manufacturer;
import com.maciu19.autobidder.api.model.VehicleModel;
import com.maciu19.autobidder.api.model.VehicleModelGeneration;
import com.maciu19.autobidder.api.repository.ManufacturerRepository;
import com.maciu19.autobidder.api.repository.VehicleModelGenerationRepository;
import com.maciu19.autobidder.api.repository.VehicleModelRepository;
import com.maciu19.autobidder.api.service.scraper.VehicleScraperService;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class VehicleServiceImpl implements VehicleService {

    private final ManufacturerRepository manufacturerRepository;
    private final VehicleModelRepository modelRepository;
    private final VehicleModelGenerationRepository vehicleModelGenerationRepository;
    private final VehicleScraperService scraperService;

    public VehicleServiceImpl(
            ManufacturerRepository manufacturerRepository,
            VehicleModelRepository modelRepository, VehicleModelGenerationRepository vehicleModelGenerationRepository,
            VehicleScraperService scraperService) {
        this.manufacturerRepository = manufacturerRepository;
        this.vehicleModelGenerationRepository = vehicleModelGenerationRepository;
        this.scraperService = scraperService;
        this.modelRepository = modelRepository;
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

    @Override
    public List<VehicleModel> getAllOrCreateVehicleModelForManufacturer(UUID manufacturerId) {
        List<VehicleModel> vehicleModels = modelRepository.findAllByManufacturer(manufacturerId);

        if (!vehicleModels.isEmpty()) {
            return vehicleModels;
        }

        vehicleModels = scraperService.getAllVehicleModelsForManufacturer(manufacturerId);

        if (!vehicleModels.isEmpty()) {
            return modelRepository.saveAll(vehicleModels);
        }

        return Collections.emptyList();
    }

    @Override
    public List<VehicleModelGeneration> getAllOrCreateModelGenerationForVehicleModel(UUID vehicleModelId) {
        List<VehicleModelGeneration> generations = vehicleModelGenerationRepository.getAllGenerationsForModel(vehicleModelId);

        if (!generations.isEmpty()) {
            return generations;
        }

        generations = scraperService.getAllModelGenerationForVehicleModel(vehicleModelId);

        if (!generations.isEmpty()) {
            return vehicleModelGenerationRepository.saveAll(generations);
        }

        return Collections.emptyList();
    }
}
