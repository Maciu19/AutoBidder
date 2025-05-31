package com.maciu19.autobidder.api.controller;

import com.maciu19.autobidder.api.controller.dtos.ManufacturerDto;
import com.maciu19.autobidder.api.controller.dtos.VehicleModelDto;
import com.maciu19.autobidder.api.service.ManufacturerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/manufacturer")
public class ManufacturerController {

    @Autowired
    private final ManufacturerService service;

    public ManufacturerController(ManufacturerService service) {
        this.service = service;
    }

    @GetMapping
    public List<ManufacturerDto> findAll() {
        return service.getAllOrCreateManufacturers()
                .stream().map(ManufacturerDto::mapToDto)
                .toList();
    }

    @GetMapping(value = "/{id}/models")
    public List<VehicleModelDto> findAllModelsForManufacturer(@PathVariable("id") UUID id) {
        return service.getAllOrCreateVehicleModelForManufacturer(id)
                .stream().map(VehicleModelDto::mapToDto)
                .toList();
    }
}
