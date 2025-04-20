package com.amine.pfe.georef_module.image.service.impl;

import java.nio.file.Path;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.amine.pfe.georef_module.entity.GeorefImage;
import com.amine.pfe.georef_module.enums.GeorefStatus;
import com.amine.pfe.georef_module.image.service.port.GeorefImageFactory;

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

