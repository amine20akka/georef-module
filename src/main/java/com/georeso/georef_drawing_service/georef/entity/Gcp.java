package com.georeso.georef_drawing_service.georef.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "gcp", schema = "georef")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Gcp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "image_id", nullable = false)
    private GeorefImage image;

    private int sourceX;

    private int sourceY;

    private Double mapX;

    private Double mapY;

    private int index;

    private String color;

    private Double residual;
}
