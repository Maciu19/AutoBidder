package com.maciu19.autobidder.api.repository;

import com.maciu19.autobidder.api.model.VehicleEngineOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VehicleEngineOptionRepository extends JpaRepository<VehicleEngineOption, UUID> {
}
