package com.maciu19.autobidder.api.model.enums;

public enum FileType implements EnumString {

    IMAGE("image"),
    VIDEO("video"),
    PDF_DOCUMENT("pdf_document"),
    SERVICE_RECORD("service_record");

    private final String id;

    FileType(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }
}
