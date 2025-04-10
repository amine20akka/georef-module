package com.georeso.georef_drawing_service.georef.enums;

public enum Srid {
    _4326, _3857;

    @Override
    public String toString() {
        return this.name();
    }
}
