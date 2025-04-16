package com.georeso.georef_drawing_service.georef.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.georeso.georef_drawing_service.georef.enums.Compression;
import com.georeso.georef_drawing_service.georef.enums.GeorefStatus;
import com.georeso.georef_drawing_service.georef.enums.ResamplingMethod;
import com.georeso.georef_drawing_service.georef.enums.Srid;
import com.georeso.georef_drawing_service.georef.enums.TransformationType;

@Entity
@Table(name = "georef_images", schema = "georef")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GeorefImage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String hash;

    @Column(nullable = false)
    private String filepathOriginal;

    private String filepathGeoreferenced;

    private LocalDateTime uploadingDate;

    private LocalDateTime lastGeoreferencingDate;

    @Enumerated(EnumType.STRING)
    private TransformationType transformationType;

    @Enumerated(EnumType.STRING)
    private Srid srid;

    @Enumerated(EnumType.STRING)
    private GeorefStatus status;

    @Enumerated(EnumType.STRING)
    private ResamplingMethod resamplingMethod;

    @Enumerated(EnumType.STRING)
    private Compression compression;

    private Double meanResidual;

    @OneToMany(mappedBy = "image", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Gcp> gcps;

    @OneToOne(mappedBy = "image", cascade = CascadeType.ALL, orphanRemoval = true)
    private GeorefLayer layer;
}