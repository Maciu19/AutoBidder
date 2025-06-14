package com.maciu19.autobidder.api.controller;

import com.maciu19.autobidder.api.controller.dtos.ManufacturerDto;
import com.maciu19.autobidder.api.controller.dtos.VehicleEngineOptionDto;
import com.maciu19.autobidder.api.controller.dtos.VehicleModelDto;
import com.maciu19.autobidder.api.controller.dtos.VehicleModelGenerationDto;
import com.maciu19.autobidder.api.model.VehicleEngineOption;
import com.maciu19.autobidder.api.service.VehicleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/vehicles")
public class VehicleController {

    @Autowired
    private final VehicleService service;

    public VehicleController(VehicleService service) {
        this.service = service;
    }

    @GetMapping(value = "/manufacturers")
    public List<ManufacturerDto> findAll() {
        return service.getAllManufacturers()
                .stream().map(ManufacturerDto::toDto)
                .toList();
    }

    @GetMapping(value = "/manufacturers/{id}/models")
    public List<VehicleModelDto> findAllModelsForManufacturer(@PathVariable("id") UUID id) {
        return service.getAllVehicleModelForManufacturer(id)
                .stream().map(VehicleModelDto::toDto)
                .toList();
    }

    @GetMapping(value = "/models/{id}/generations")
    public List<VehicleModelGenerationDto> findAllModelGenerationsForModel(@PathVariable("id") UUID id) {
        return service.getAllModelGenerationForVehicleModel(id)
                .stream().map(VehicleModelGenerationDto::toDto)
                .toList();
    }

    @GetMapping(value = "/generations/{id}/engines")
    public List<VehicleEngineOptionDto> findAllEnginesForGeneration(@PathVariable("id") UUID id) {
        return service.getAllEngineOptionsForModelGeneration(id)
                .stream().map(VehicleEngineOptionDto::toDto)
                .toList();
    }
}
