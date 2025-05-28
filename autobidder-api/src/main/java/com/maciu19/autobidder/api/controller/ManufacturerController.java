package com.maciu19.autobidder.api.controller;

import com.maciu19.autobidder.api.model.Manufacturer;
import com.maciu19.autobidder.api.service.ManufacturerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/manufacturer")
public class ManufacturerController {

    @Autowired
    private final ManufacturerService service;

    public ManufacturerController(ManufacturerService service) {
        this.service = service;
    }

    @GetMapping
    public List<Manufacturer> findAll() {
        return service.getAllOrCreateManufacturers();
    }
}
