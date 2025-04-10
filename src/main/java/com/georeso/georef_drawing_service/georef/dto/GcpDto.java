package com.georeso.georef_drawing_service.georef.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GcpDto {
    private Long id;
    private Long imageId;
    private int sourceX;
    private int sourceY;
    private Double mapX;
    private Double mapY;
    private int index;
    private String color;
    private Double residual;
}
