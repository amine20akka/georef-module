package com.georeso.georef_drawing_service.georef.enums;

public enum Compression {
    NONE, LZW, JPEG, DEFLATE;

    @Override
    public String toString() {
        return this.name();
    }

}
