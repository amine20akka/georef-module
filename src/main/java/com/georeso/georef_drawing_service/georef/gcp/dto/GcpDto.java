package com.georeso.georef_drawing_service.georef.gcp.dto;

import java.util.UUID;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GcpDto {
    private UUID id;
    private UUID imageId;
    private int sourceX;
    private int sourceY;
    private Double mapX;
    private Double mapY;
    private int index;
    private Double residual;
}
