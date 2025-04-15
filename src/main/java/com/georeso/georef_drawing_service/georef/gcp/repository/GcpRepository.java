package com.georeso.georef_drawing_service.georef.gcp.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.georeso.georef_drawing_service.georef.entity.Gcp;

public interface GcpRepository extends JpaRepository<Gcp, UUID> {
    List<Gcp> findByImageId(UUID imageId);
}
