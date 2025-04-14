package com.georeso.georef_drawing_service.georef.service.impl;

import java.nio.file.Path;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.georeso.georef_drawing_service.georef.entity.GeorefImage;
import com.georeso.georef_drawing_service.georef.enums.GeorefStatus;
import com.georeso.georef_drawing_service.georef.service.port.GeorefImageFactory;

@Component
public class DefaultGeorefImageFactory implements GeorefImageFactory {
    @Override
    public GeorefImage create(String hash, Path path) {
        GeorefImage image = new GeorefImage();
        image.setHash(hash);
        image.setFilepathOriginal(path.toString());
        image.setUploadingDate(LocalDateTime.now());
        image.setStatus(GeorefStatus.UPLOADED);
        return image;
    }
}

