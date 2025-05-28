package com.maciu19.autobidder.api.repository;

import com.maciu19.autobidder.api.model.VehicleModelGeneration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VehicleModelGenerationRepository extends JpaRepository<VehicleModelGeneration, UUID> {
}
