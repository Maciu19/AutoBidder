package com.maciu19.autobidder.api.controller;

import com.maciu19.autobidder.api.controller.dtos.ManufacturerDto;
import com.maciu19.autobidder.api.controller.dtos.VehicleEngineOptionDto;
import com.maciu19.autobidder.api.controller.dtos.VehicleModelDto;
import com.maciu19.autobidder.api.controller.dtos.VehicleModelGenerationDto;
import com.maciu19.autobidder.api.model.dtos.VehicleInfo;
import com.maciu19.autobidder.api.service.VehicleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
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
    public ResponseEntity<List<ManufacturerDto>> findAll() {
        List<ManufacturerDto> dtos = service.getAllManufacturers()
                .stream().map(ManufacturerDto::toDto)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    @GetMapping(value = "/manufacturers/{id}/models")
    public ResponseEntity<List<VehicleModelDto>> findAllModelsForManufacturer(@PathVariable("id") UUID id) {
        List<VehicleModelDto> dtos = service.getAllVehicleModelForManufacturer(id)
                .stream().map(VehicleModelDto::toDto)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    @GetMapping(value = "/models/{id}/generations")
    public ResponseEntity<List<VehicleModelGenerationDto>> findAllModelGenerationsForModel(@PathVariable("id") UUID id) {
        List<VehicleModelGenerationDto> dtos = service.getAllModelGenerationForVehicleModel(id)
                .stream().map(VehicleModelGenerationDto::toDto)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    @GetMapping(value = "/generations/{id}/engines")
    public ResponseEntity<List<VehicleEngineOptionDto>> findAllEnginesForGeneration(@PathVariable("id") UUID id) {
        List<VehicleEngineOptionDto> dtos = service.getAllEngineOptionsForModelGeneration(id)
                .stream().map(VehicleEngineOptionDto::toDto)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/info/{id}")
    public ResponseEntity<VehicleInfo> getVehicleInfoById(@PathVariable UUID id) {
        Optional<VehicleInfo> vehicleInfo = service.getVehicleInfo(id);

        return vehicleInfo.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
