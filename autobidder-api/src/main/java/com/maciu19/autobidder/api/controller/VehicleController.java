package com.maciu19.autobidder.api.controller;

import com.maciu19.autobidder.api.controller.dtos.ManufacturerDto;
import com.maciu19.autobidder.api.controller.dtos.VehicleModelDto;
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
        return service.getAllOrCreateManufacturers()
                .stream().map(ManufacturerDto::mapToDto)
                .toList();
    }

    @GetMapping(value = "/manufacturers/{id}/models")
    public List<VehicleModelDto> findAllModelsForManufacturer(@PathVariable("id") UUID id) {
        return service.getAllOrCreateVehicleModelForManufacturer(id)
                .stream().map(VehicleModelDto::mapToDto)
                .toList();
    }
}
