package com.maciu19.autobidder.api.repository;

import com.maciu19.autobidder.api.model.VehicleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface VehicleModelRepository extends JpaRepository<VehicleModel, UUID> {

    @Query("select v from VehicleModel v where v.manufacturer.id = ?1")
    List<VehicleModel> findAllByManufacturer(UUID id);
}
