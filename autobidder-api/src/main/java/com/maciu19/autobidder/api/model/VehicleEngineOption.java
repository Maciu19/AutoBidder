package com.maciu19.autobidder.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Table(name = "vehicle_engine_options", indexes = {
        @Index(name = "idx_vehicleengineoption", columnList = "vehicle_model_generation_id, name")
})
@Entity
@NoArgsConstructor
public class VehicleEngineOption {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID id;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "cylinders")
    private Integer cylinders;

    @ManyToOne(optional = false)
    @JoinColumn(name = "vehicle_model_generation_id", nullable = false)
    private VehicleModelGeneration vehicleModelGeneration;

    @Column(name = "displacement")
    private Integer displacement;

    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_type")
    private FuelType fuelType;

    @Enumerated(EnumType.STRING)
    @Column(name = "transmission_type")
    private TransmissionType transmissionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "drivetrain")
    private Drivetrain drivetrain;

    @Column(name = "power_hp")
    private Integer powerHp;

    @Column(name = "torque_nm")
    private Integer torqueNm;
}
