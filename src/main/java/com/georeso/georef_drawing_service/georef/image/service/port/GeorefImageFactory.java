package com.georeso.georef_drawing_service.georef.image.service.port;

import java.nio.file.Path;

import com.georeso.georef_drawing_service.georef.entity.GeorefImage;

public interface GeorefImageFactory {
    GeorefImage create(String hash, Path path);
}
