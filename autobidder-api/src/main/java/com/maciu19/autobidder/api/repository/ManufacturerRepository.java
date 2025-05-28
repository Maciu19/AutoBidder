package com.maciu19.autobidder.api.repository;

import com.maciu19.autobidder.api.model.Manufacturer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ManufacturerRepository extends JpaRepository<Manufacturer, UUID> {

}