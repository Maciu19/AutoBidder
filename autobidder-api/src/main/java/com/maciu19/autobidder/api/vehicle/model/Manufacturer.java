package com.maciu19.autobidder.api.vehicle.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "vehicle_manufacturers")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Manufacturer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID id;

    @Column(name = "url", unique = true)
    private String url;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @OneToMany(
            mappedBy = "manufacturer",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Set<VehicleModel> vehicleModels = new LinkedHashSet<>();

    @CreatedDate
    @Column(name = "created_date")
    private Instant createdDate;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private Instant lastModifiedDate;

    public void addVehicleModel(VehicleModel model) {
        vehicleModels.add(model);
        model.setManufacturer(this);
    }
}
