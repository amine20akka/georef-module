package com.amine.pfe.georef_module.enums;

public enum GeorefStatus {
    UPLOADED, PENDING, PROCESSING, COMPLETED, FAILED;

    @Override
    public String toString() {
        return this.name();
    }
}
