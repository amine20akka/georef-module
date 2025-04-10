package com.georeso.georef_drawing_service.georef.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.georeso.georef_drawing_service.georef.entity.Gcp;

public interface GcpRepository extends JpaRepository<Gcp, Long> {
    List<Gcp> findByImageId(Long imageId);
}
