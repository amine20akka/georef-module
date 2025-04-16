package com.georeso.georef_drawing_service.georef.entity;

import java.util.UUID;

import com.georeso.georef_drawing_service.georef.enums.LayerStatus;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "georef_layers", schema = "georef")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GeorefLayer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "image_id", nullable = false, unique = true)
    private GeorefImage image;

    private String workspace;

    private String storeName;

    private String layerName;

    @Enumerated(EnumType.STRING)
    private LayerStatus status;
}
