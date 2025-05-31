package com.maciu19.autobidder.api.model;

import com.maciu19.autobidder.api.model.enums.VehicleModelSegment;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "vehicle_models", uniqueConstraints = {
        @UniqueConstraint(name = "uc_vehiclemodel_name", columnNames = {"NAME", "manufacturer_id"})
})
@NoArgsConstructor
public class VehicleModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID id;

    @Column(name = "NAME", length = 100, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "segment", length = 20)
    private VehicleModelSegment vehicleModelSegment;

    @OneToMany(mappedBy = "vehicleModel", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<VehicleModelGeneration> vehicleModelGenerations = new LinkedHashSet<>();

    @ManyToOne(optional = false)
    @JoinColumn(name = "manufacturer_id", nullable = false)
    private Manufacturer manufacturer;

    @CreatedDate
    @Column(name = "created_date")
    private Instant createdDate;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private Instant lastModifiedDate;
}
