package com.maciu19.autobidder.api.repository;

import com.maciu19.autobidder.api.model.VehicleEngineOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface VehicleEngineOptionRepository extends JpaRepository<VehicleEngineOption, UUID> {
    @Query("select v from VehicleEngineOption v where v.vehicleModelGeneration.id = ?1")
    List<VehicleEngineOption> findByModelGeneration(UUID id);
}
