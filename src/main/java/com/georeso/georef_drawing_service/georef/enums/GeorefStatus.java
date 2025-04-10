package com.georeso.georef_drawing_service.georef.enums;

public enum GeorefStatus {
    UPLOADED, PENDING, PROCESSING, COMPLETED, FAILED;

    @Override
    public String toString() {
        return this.name();
    }
}
