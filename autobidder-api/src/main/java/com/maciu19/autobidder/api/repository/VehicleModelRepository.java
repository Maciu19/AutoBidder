package com.maciu19.autobidder.api.repository;

import com.maciu19.autobidder.api.model.VehicleModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VehicleModelRepository extends JpaRepository<VehicleModel, UUID> {
}
