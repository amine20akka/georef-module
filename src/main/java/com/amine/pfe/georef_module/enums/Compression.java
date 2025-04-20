package com.amine.pfe.georef_module.enums;

public enum Compression {
    NONE, LZW, JPEG, DEFLATE;

    @Override
    public String toString() {
        return this.name();
    }

}
