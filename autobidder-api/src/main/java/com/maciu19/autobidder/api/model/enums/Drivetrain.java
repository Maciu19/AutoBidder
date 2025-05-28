package com.maciu19.autobidder.api.model.enums;

public enum Drivetrain implements EnumString {

    FWD("fwd"),
    RWD("rwd"),
    AWD("awd"),
    FOOUR_WD("4wd");

    private final String id;

    Drivetrain(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }
}
