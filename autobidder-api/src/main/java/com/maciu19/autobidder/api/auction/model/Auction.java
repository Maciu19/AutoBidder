package com.maciu19.autobidder.api.auction.model;

import com.maciu19.autobidder.api.user.model.User;
import com.maciu19.autobidder.api.vehicle.model.VehicleEngineOption;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "auction")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_engine_option_id", nullable = false)
    private VehicleEngineOption vehicleEngineOption;

    @Column(name = "vin", unique = true, nullable = false)
    private String vin;

    @Column(name = "location")
    private String location;

    @Column(name = "starting_price")
    private Double startingPrice;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "steering_wheel_side", length = 50)
    private SteeringWheelSide steeringWheelSide;

    @Column(name = "has_warranty")
    private boolean hasWarranty;

    @Column(name = "no_crash_registered")
    private boolean noCrashRegistered;

    @Column(name = "make_year")
    private Year makeYear;

    @Column(name = "mileage")
    private int mileage;

    @Column(name = "exterior_color")
    private String exteriorColor;

    @Column(name = "interior_color")
    private String interiorColor;

    @OneToMany(
            mappedBy = "auction",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<MediaAsset> mediaAssets = new ArrayList<>();

    @ElementCollection(targetClass = Feature.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "auction_features", joinColumns = @JoinColumn(name = "auction_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "feature", nullable = false)
    private Set<Feature> features = new HashSet<>();

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "modifications")
    private String modifications;

    @Column(name = "knownFlaws")
    private String knownFlaws;

    @Column(name = "recent_service_history")
    private String recentServiceHistory;

    @Column(name = "other_items_included")
    private String otherItemsIncluded;

    @Column(name = "ownershipHistory")
    private String ownershipHistory;

    @CreatedDate
    @Column(name = "created_date")
    private Instant createdDate;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private Instant lastModifiedDate;

    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();

        return getStartTime() != null && getEndTime() != null &&
                getStartTime().isBefore(now) &&
                getEndTime().isAfter(now);
    }

    public void addMediaAsset(MediaAsset mediaAsset) {
        if (mediaAsset == null) {
            throw new IllegalStateException("Media asset cannot be null");
        }

        mediaAssets.add(mediaAsset);
    }
}
