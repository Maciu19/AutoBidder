package com.maciu19.autobidder.api.model;

import com.maciu19.autobidder.api.model.enums.Drivetrain;
import com.maciu19.autobidder.api.model.enums.FuelType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Getter
@Setter
@Table(name = "vehicle_engine_options", indexes = {
        @Index(name = "idx_vehicleengineoption", columnList = "vehicle_model_generation_id, name")
})
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class VehicleEngineOption {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID id;

    @Column(name = "name", length = 100)
    private String name;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_model_generation_id", nullable = false)
    private VehicleModelGeneration vehicleModelGeneration;

    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_type")
    private FuelType fuelType;

    @Enumerated(EnumType.STRING)
    @Column(name = "drivetrain")
    private Drivetrain drivetrain;

    @Column(name = "power_hp")
    private Integer powerHp;

    @Column(name = "torque_nm")
    private Integer torqueNm;

    @Column(name = "displacement")
    private Integer displacement;

    @Column(name = "cylinders")
    private String cylinders;

    @Column(name = "transmission_type")
    private String transmissionType;
}
