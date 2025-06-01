package com.maciu19.autobidder.api.model.enums;

public enum FuelType implements EnumString{

    GASOLINE("gasoline"),
    DIESEL("diesel"),
    HYBRID("hybrid"),
    ELECTRIC("electric");

    private final String id;

    FuelType(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }
}
