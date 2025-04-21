package com.amine.pfe.georef_module.image.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.amine.pfe.georef_module.enums.GeorefStatus;
import com.amine.pfe.georef_module.enums.Srid;
import com.amine.pfe.georef_module.enums.TransformationType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GeorefImageWithFileDto {
    private UUID id;
    private String hash;
    private String filepathOriginal;
    private LocalDateTime uploadingDate;
    private GeorefStatus status;
    private Srid srid;
    private TransformationType transformationType;
    private String imageBase64;
}
