package com.maciu19.autobidder.api.vehicle.repository;

import com.maciu19.autobidder.api.vehicle.model.VehicleEngineOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VehicleEngineOptionRepository extends JpaRepository<VehicleEngineOption, UUID> {

    @Query("SELECT veo FROM VehicleEngineOption veo " +
            "JOIN FETCH veo.vehicleModelGeneration vmg " +
            "JOIN FETCH vmg.vehicleModel vm " +
            "JOIN FETCH vm.manufacturer m " +
            "WHERE veo.id = :id")
    Optional<VehicleEngineOption> findByIdWithDetails(@Param("id") UUID id);

    @Query("select v from VehicleEngineOption v where v.vehicleModelGeneration.id = ?1")
    List<VehicleEngineOption> findByModelGeneration(UUID id);
}
