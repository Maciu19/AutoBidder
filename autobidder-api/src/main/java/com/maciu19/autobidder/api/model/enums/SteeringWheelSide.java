package com.maciu19.autobidder.api.model.enums;

public enum SteeringWheelSide implements EnumString {
    LEFT("left"),
    RIGHT("right");

    private final String id;

    SteeringWheelSide(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }
}
