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
@Table(name = "vehicle_models", uniqueConstraints = {
        @UniqueConstraint(name = "uc_vehiclemodel_name", columnNames = {"name", "manufacturer_id"})
})
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class VehicleModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID id;

    @Column(name = "url", unique = true)
    private String url;

    @Column(name = "NAME", length = 100, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "segment", length = 20)
    private VehicleModelSegment vehicleModelSegment;

    @OneToMany(
            mappedBy = "vehicleModel",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Set<VehicleModelGeneration> vehicleModelGenerations = new LinkedHashSet<>();

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "manufacturer_id", nullable = false)
    private Manufacturer manufacturer;

    @CreatedDate
    @Column(name = "created_date")
    private Instant createdDate;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private Instant lastModifiedDate;

    public void addGeneration(VehicleModelGeneration generation) {
        vehicleModelGenerations.add(generation);
        generation.setVehicleModel(this);
    }
}
