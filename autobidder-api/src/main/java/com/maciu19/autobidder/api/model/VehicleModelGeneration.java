package com.maciu19.autobidder.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
@EntityListeners(AuditingEntityListener.class)
public class VehicleModelGeneration {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_model_id", nullable = false)
    private VehicleModel vehicleModel;

    @OneToMany(mappedBy = "vehicleModelGeneration", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
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

    public VehicleModelGeneration(VehicleModel vehicleModel, Integer startYear, Integer endYear) {
        this.vehicleModel = vehicleModel;
        this.startYear = startYear;
        this.endYear = endYear;
    }
}
