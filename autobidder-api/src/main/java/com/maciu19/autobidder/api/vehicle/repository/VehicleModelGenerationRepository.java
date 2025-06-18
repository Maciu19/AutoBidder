package com.maciu19.autobidder.api.vehicle.repository;

import com.maciu19.autobidder.api.vehicle.model.VehicleModelGeneration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VehicleModelGenerationRepository extends JpaRepository<VehicleModelGeneration, UUID> {
    @Query("select v from VehicleModelGeneration v where v.vehicleModel.id = ?1")
    List<VehicleModelGeneration> getAllGenerationsForModel(UUID id);
}
