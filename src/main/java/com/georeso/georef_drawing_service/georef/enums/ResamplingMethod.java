package com.georeso.georef_drawing_service.georef.enums;

public enum ResamplingMethod {
    NEAREST, BILINEAR, CUBIC;

    @Override
    public String toString() {
        return this.name();
    }
}

