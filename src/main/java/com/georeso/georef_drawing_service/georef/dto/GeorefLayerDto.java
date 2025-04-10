package com.georeso.georef_drawing_service.georef.dto;

import com.georeso.georef_drawing_service.georef.enums.LayerStatus;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GeorefLayerDto {
    private Long id;
    private Long imageId;
    private String workspace;
    private String storeName;
    private String layerName;
    private LayerStatus status;
}
