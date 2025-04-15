package com.georeso.georef_drawing_service.georef.image.dto;

import java.util.UUID;

import com.georeso.georef_drawing_service.georef.enums.LayerStatus;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GeorefLayerDto {
    private UUID id;
    private UUID imageId;
    private String workspace;
    private String storeName;
    private String layerName;
    private LayerStatus status;
}
