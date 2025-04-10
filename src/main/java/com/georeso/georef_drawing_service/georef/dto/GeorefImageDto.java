package com.georeso.georef_drawing_service.georef.dto;

import com.georeso.georef_drawing_service.georef.enums.Compression;
import com.georeso.georef_drawing_service.georef.enums.GeorefStatus;
import com.georeso.georef_drawing_service.georef.enums.ResamplingMethod;
import com.georeso.georef_drawing_service.georef.enums.Srid;
import com.georeso.georef_drawing_service.georef.enums.TransformationType;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GeorefImageDto {

    private Long id;
    private String filepathOriginal;
    private String filepathGeoreferenced;
    private LocalDateTime uploadingDate;
    private LocalDateTime lastGeoreferencingDate;
    private TransformationType transformationType;
    private Srid srid;
    private GeorefStatus status;
    private ResamplingMethod resamplingMethod;
    private Compression compression;
    private Integer gcpsCount;
    private Double meanResidual;

    private List<GcpDto> gcps;
    private GeorefLayerDto layer;
}
