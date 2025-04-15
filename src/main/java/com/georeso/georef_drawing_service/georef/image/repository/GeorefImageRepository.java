package com.georeso.georef_drawing_service.georef.image.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.georeso.georef_drawing_service.georef.entity.GeorefImage;

public interface GeorefImageRepository extends JpaRepository<GeorefImage, UUID> {
    boolean existsByHash(String hash);
    GeorefImage findByHash(String hash);
}