package com.amine.pfe.georef_module.gcp.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amine.pfe.georef_module.entity.Gcp;

public interface GcpRepository extends JpaRepository<Gcp, UUID> {
    List<Gcp> findByImageId(UUID imageId);
    boolean existsByImageIdAndIndex(UUID imageId, int index);
    List<Gcp> findAllByImageIdOrderByIndex(UUID imageId);
}
