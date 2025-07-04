package com.maciu19.autobidder.api.vehicle;

import com.maciu19.autobidder.api.auction.model.Feature;
import com.maciu19.autobidder.api.vehicle.dto.*;
import com.maciu19.autobidder.api.vehicle.mapper.VehicleInfoMapper;
import com.maciu19.autobidder.api.vehicle.service.VehicleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/vehicles")
public class VehicleController {

    @Autowired
    private final VehicleService service;

    @Autowired
    private final VehicleInfoMapper vehicleInfoMapper;

    public VehicleController(VehicleService service, VehicleInfoMapper vehicleInfoMapper) {
        this.service = service;
        this.vehicleInfoMapper = vehicleInfoMapper;
    }

    @GetMapping(value = "/manufacturers")
    public ResponseEntity<List<ManufacturerDto>> findAll() {
        List<ManufacturerDto> dtos = service.getAllManufacturers()
                .stream().map(vehicleInfoMapper::toDto)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    @GetMapping(value = "/manufacturers/{id}/models")
    public ResponseEntity<List<VehicleModelDto>> findAllModelsForManufacturer(@PathVariable("id") UUID id) {
        List<VehicleModelDto> dtos = service.getAllVehicleModelForManufacturer(id)
                .stream().map(vehicleInfoMapper::toDto)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    @GetMapping(value = "/models/{id}/generations")
    public ResponseEntity<List<VehicleModelGenerationDto>> findAllModelGenerationsForModel(@PathVariable("id") UUID id) {
        List<VehicleModelGenerationDto> dtos = service.getAllModelGenerationForVehicleModel(id)
                .stream().map(vehicleInfoMapper::toDto)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    @GetMapping(value = "/generations/{id}/engines")
    public ResponseEntity<List<VehicleEngineOptionDto>> findAllEnginesForGeneration(@PathVariable("id") UUID id) {
        List<VehicleEngineOptionDto> dtos = service.getAllEngineOptionsForModelGeneration(id)
                .stream().map(vehicleInfoMapper::toDto)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/info/{id}")
    public ResponseEntity<VehicleInfo> getVehicleInfoById(@PathVariable UUID id) {
        Optional<VehicleInfo> vehicleInfo = service.getVehicleInfo(id);

        return vehicleInfo.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/features")
    public ResponseEntity<Map<String, List<String>>> getAllFeaturesGroupByCategory() {
        Map<String, List<String>> result = Arrays.stream(Feature.values())
                .collect(Collectors.groupingBy(
                        Feature::getCategory,
                        Collectors.mapping(
                                Feature::getId,
                                Collectors.toList()
                        )
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().name(),
                        Map.Entry::getValue
                ));

        return ResponseEntity.ok(result);
    }
}
