package com.maciu19.autobidder.api.model.enums;

public enum Drivetrain implements EnumString {

    FRONT_WHEEL_DRIVE("front-wheel-drive"),
    REAR_WHEEL_DRIVE("read-wheel-drive"),
    ALL_WHEEL_DRIVE("all-wheel-drive");

    private final String id;

    Drivetrain(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }
}
