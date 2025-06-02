package com.maciu19.autobidder.api.repository;

import com.maciu19.autobidder.api.model.Manufacturer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface ManufacturerRepository extends JpaRepository<Manufacturer, UUID> {
    
    @Query("select m from Manufacturer m where m.url = ?1")
    Optional<Manufacturer> findByUrl(String url);
}