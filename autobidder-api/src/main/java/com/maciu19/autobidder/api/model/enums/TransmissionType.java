package com.maciu19.autobidder.api.model.enums;

public enum TransmissionType implements EnumString {

    MANUAL("manual"),
    AUTOMATIC("automatic"),
    SEMI_AUTOMATIC("semi_automatic");

    private final String id;

    TransmissionType(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }
}
