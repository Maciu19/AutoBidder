package com.maciu19.autobidder.api.vehicle.model;

import com.maciu19.autobidder.api.shared.enums.EnumString;

public enum FuelType implements EnumString {

    GASOLINE("gasoline"),
    DIESEL("diesel"),
    ELECTRIC("electric"),
    HYBRID("hybrid"),
    MILD_HYBRID("mild-hybrid"),
    PLUG_IN_HYBRID("plug-in-hybrid"),
    NATURAL_GAS("natural-gas"),
    LPG("lpg"),
    ETHANOL("ethanol"),
    UNKNOWN("unknown");


    private final String id;

    FuelType(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }
}
