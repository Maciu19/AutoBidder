package com.maciu19.autobidder.api.auction.model;

import com.maciu19.autobidder.api.shared.enums.EnumString;

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
