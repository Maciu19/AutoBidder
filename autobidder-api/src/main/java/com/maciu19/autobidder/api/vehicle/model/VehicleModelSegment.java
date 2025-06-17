package com.maciu19.autobidder.api.vehicle.model;

import com.maciu19.autobidder.api.shared.enums.EnumString;

public enum VehicleModelSegment implements EnumString {

    CONVERTIBLE("convertible"),
    COUPE("coupe"),
    HATCHBACK("hatchback"),
    VAN("van"),
    SEDAN("sedan"),
    SUV("suv"),
    TRUCK("truck"),
    WAGON("wagon");

    private final String id;

    VehicleModelSegment(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }
}
