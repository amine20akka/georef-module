package com.amine.pfe.georef_module.enums;

public enum LayerStatus {
    PENDING, PUBLISHED, FAILED;

    @Override
    public String toString() {
        return this.name();
    }
}

