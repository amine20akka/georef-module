package com.georeso.georef_drawing_service.georef.enums;

public enum LayerStatus {
    PENDING, PUBLISHED, FAILED;

    @Override
    public String toString() {
        return this.name();
    }
}

