package com.amine.pfe.georef_module.enums;

public enum ResamplingMethod {
    NEAREST, BILINEAR, CUBIC;

    @Override
    public String toString() {
        return this.name();
    }
}

