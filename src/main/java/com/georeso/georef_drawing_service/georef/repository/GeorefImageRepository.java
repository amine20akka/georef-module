package com.georeso.georef_drawing_service.georef.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.georeso.georef_drawing_service.georef.entity.GeorefImage;

public interface GeorefImageRepository extends JpaRepository<GeorefImage, Long> {
}