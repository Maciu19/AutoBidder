package com.maciu19.autobidder.api.model.enums;

public enum VehicleModelSegment implements EnumString {

    MINI("mini"),
    SMALL("small"),
    COMPACT("compact"),
    MID_SIZE("mid_size"),
    EXECUTE("execute"),
    LUXURY("luxury"),
    SUV("suv"),
    MPV("mpv"),
    SPORT("sport");

    private final String id;

    VehicleModelSegment(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }
}
