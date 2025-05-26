package com.maciu19.autobidder.api.model;

public enum FuelType implements EnumString{

    PETROL("petrol"),
    DIESEL("diesel"),
    ELECTRIC("electric"),
    HYBRID("hybrid"),
    PLUG_IN_HYBRID("plug_in_hybrid"),
    LPG("lpg"),
    CNG("cng"),
    HYDROGEN("hydrogen"),
    OTHER("other");

    private final String id;

    FuelType(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }
}
