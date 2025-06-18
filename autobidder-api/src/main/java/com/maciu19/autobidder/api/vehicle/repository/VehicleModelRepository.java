package com.maciu19.autobidder.api.vehicle.repository;

import com.maciu19.autobidder.api.vehicle.model.VehicleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VehicleModelRepository extends JpaRepository<VehicleModel, UUID> {

    @Query("select v from VehicleModel v where v.url = ?1")
    Optional<VehicleModel> findByUrl(String url);

    @Query("select v from VehicleModel v where v.manufacturer.id = ?1")
    List<VehicleModel> findAllByManufacturer(UUID id);
}
