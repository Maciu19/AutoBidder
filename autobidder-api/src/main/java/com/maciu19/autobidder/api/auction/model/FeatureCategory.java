package com.maciu19.autobidder.api.auction.model;

import com.maciu19.autobidder.api.shared.enums.EnumString;

public enum FeatureCategory implements EnumString {

    AUDIO_TECHNOLOGY("audio_technology"),
    COMFORT_EQUIPMENT("comfort_equipment"),
    ELECTRONICS_ASSISTANCE("electronics_assitance"),
    PERFORMANCE("performance"),
    SAFETY("safety");

    private String id;

    FeatureCategory(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }
}
