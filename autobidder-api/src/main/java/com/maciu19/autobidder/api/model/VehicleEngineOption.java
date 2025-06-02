package com.maciu19.autobidder.api.model;

import com.maciu19.autobidder.api.model.enums.Drivetrain;
import com.maciu19.autobidder.api.model.enums.FuelType;
import jakarta.persistence.*;
import lombok.*;

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
public class VehicleEngineOption {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID id;

    @Lob
    @Column(name = "url", unique = true)
    private String url;

    @Column(name = "name")
    private String name;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_model_generation_id", nullable = false)
    private VehicleModelGeneration vehicleModelGeneration;

    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_type", length = 100)
    private FuelType fuelType;

    @Enumerated(EnumType.STRING)
    @Column(name = "drivetrain", length = 50)
    private Drivetrain drivetrain;

    @Column(name = "power_hp")
    private Integer powerHp;

    @Column(name = "torque_nm")
    private Integer torqueNm;

    @Column(name = "displacement")
    private Integer displacement;

    @Column(name = "transmission_type")
    private String transmissionType;
}
