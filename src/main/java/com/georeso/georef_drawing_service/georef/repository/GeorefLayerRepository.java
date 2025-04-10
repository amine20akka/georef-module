package com.georeso.georef_drawing_service.georef.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.georeso.georef_drawing_service.georef.entity.GeorefLayer;

public interface GeorefLayerRepository extends JpaRepository<GeorefLayer, Long> {
    Optional<GeorefLayer> findByImageId(Long imageId);
}
