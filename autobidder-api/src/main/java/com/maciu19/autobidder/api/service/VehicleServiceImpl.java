package com.maciu19.autobidder.api.service;

import com.maciu19.autobidder.api.model.Manufacturer;
import com.maciu19.autobidder.api.model.VehicleEngineOption;
import com.maciu19.autobidder.api.model.VehicleModel;
import com.maciu19.autobidder.api.model.VehicleModelGeneration;
import com.maciu19.autobidder.api.model.dtos.VehicleInfo;
import com.maciu19.autobidder.api.model.dtos.VehicleInfoMapper;
import com.maciu19.autobidder.api.repository.ManufacturerRepository;
import com.maciu19.autobidder.api.repository.VehicleEngineOptionRepository;
import com.maciu19.autobidder.api.repository.VehicleModelGenerationRepository;
import com.maciu19.autobidder.api.repository.VehicleModelRepository;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class VehicleServiceImpl implements VehicleService {

    private final ManufacturerRepository manufacturerRepository;
    private final VehicleModelRepository modelRepository;
    private final VehicleModelGenerationRepository vehicleModelGenerationRepository;
    private final VehicleEngineOptionRepository vehicleEngineOptionRepository;
    private final VehicleInfoMapper vehicleInfoMapper;

    public VehicleServiceImpl(
            ManufacturerRepository manufacturerRepository,
            VehicleModelRepository modelRepository,
            VehicleModelGenerationRepository vehicleModelGenerationRepository,
            VehicleEngineOptionRepository vehicleEngineOptionRepository,
            VehicleInfoMapper vehicleInfoMapper) {
        this.manufacturerRepository = manufacturerRepository;
        this.vehicleModelGenerationRepository = vehicleModelGenerationRepository;
        this.modelRepository = modelRepository;
        this.vehicleEngineOptionRepository = vehicleEngineOptionRepository;
        this.vehicleInfoMapper = vehicleInfoMapper;
    }

    @Override
    public List<Manufacturer> getAllManufacturers() {
        return manufacturerRepository.findAll();
    }

    @Override
    public List<VehicleModel> getAllVehicleModelForManufacturer(UUID manufacturerId) {
        return modelRepository.findAllByManufacturer(manufacturerId);
    }

    @Override
    public List<VehicleModelGeneration> getAllModelGenerationForVehicleModel(UUID vehicleModelId) {
        return vehicleModelGenerationRepository.getAllGenerationsForModel(vehicleModelId);
    }

    @Override
    public List<VehicleEngineOption> getAllEngineOptionsForModelGeneration(UUID vehicleModelGenerationId) {
        return vehicleEngineOptionRepository.findByModelGeneration(vehicleModelGenerationId);
    }

    @Override
    public Optional<VehicleInfo> getVehicleInfo(UUID vehicleEngineOptionId) {
        Optional<VehicleEngineOption> engineOption = vehicleEngineOptionRepository.findByIdWithDetails(vehicleEngineOptionId);

        return engineOption.map(vehicleInfoMapper::toVehicleInfo);
    }
}
