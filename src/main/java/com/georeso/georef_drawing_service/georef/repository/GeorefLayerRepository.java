package com.georeso.georef_drawing_service.georef.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.georeso.georef_drawing_service.georef.entity.GeorefLayer;

public interface GeorefLayerRepository extends JpaRepository<GeorefLayer, UUID> {
    Optional<GeorefLayer> findByImageId(UUID imageId);
}
