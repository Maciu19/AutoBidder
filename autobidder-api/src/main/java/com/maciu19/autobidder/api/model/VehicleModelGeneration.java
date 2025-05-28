package com.maciu19.autobidder.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "vehicle_model_generations", uniqueConstraints = {
        @UniqueConstraint(name = "uc_vehiclemodelgeneration", columnNames = {"vehicle_model_id", "name", "start_year"})
})
@NoArgsConstructor
public class VehicleModelGeneration {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID id;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @ManyToOne(optional = false)
    @JoinColumn(name = "vehicle_model_id", nullable = false)
    private VehicleModel vehicleModel;

    @OneToMany(mappedBy = "vehicleModelGeneration", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<VehicleEngineOption> vehicleEngineOptions = new LinkedHashSet<>();

    @Min(0)
    @Column(name = "start_year", nullable = false)
    private Integer startYear;

    @Column(name = "end_year")
    private Integer endYear;

    @CreatedDate
    @Column(name = "created_date")
    private Instant createdDate;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private Instant lastModifiedDate;
}
