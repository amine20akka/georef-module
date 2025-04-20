package com.amine.pfe.georef_module.image.mapper;

import com.amine.pfe.georef_module.image.dto.*;
import com.amine.pfe.georef_module.entity.*;
public class ImageMapper {

    // -------- GeorefImage --------
    public static GeorefImageDto toDto(GeorefImage entity) {
        return GeorefImageDto.builder()
                .id(entity.getId())
                .hash(entity.getHash())
                .filepathOriginal(entity.getFilepathOriginal())
                .filepathGeoreferenced(entity.getFilepathGeoreferenced())
                .uploadingDate(entity.getUploadingDate())
                .lastGeoreferencingDate(entity.getLastGeoreferencingDate())
                .transformationType(entity.getTransformationType())
                .srid(entity.getSrid())
                .status(entity.getStatus())
                .resamplingMethod(entity.getResamplingMethod())
                .compression(entity.getCompression())
                .meanResidual(entity.getMeanResidual())
                .build();
    }

    public static GeorefImage toEntity(GeorefImageDto dto) {
        GeorefImage entity = new GeorefImage();
        entity.setId(dto.getId());
        entity.setHash(dto.getHash());
        entity.setFilepathOriginal(dto.getFilepathOriginal());
        entity.setFilepathGeoreferenced(dto.getFilepathGeoreferenced());
        entity.setUploadingDate(dto.getUploadingDate());
        entity.setLastGeoreferencingDate(dto.getLastGeoreferencingDate());
        entity.setTransformationType(dto.getTransformationType());
        entity.setSrid(dto.getSrid());
        entity.setStatus(dto.getStatus());
        entity.setResamplingMethod(dto.getResamplingMethod());
        entity.setCompression(dto.getCompression());
        entity.setMeanResidual(dto.getMeanResidual());
        return entity;
    }

    // -------- GeorefLayer --------
    public static GeorefLayerDto toDto(GeorefLayer entity) {
        return GeorefLayerDto.builder()
                .id(entity.getId())
                .imageId(entity.getImage().getId())
                .workspace(entity.getWorkspace())
                .storeName(entity.getStoreName())
                .layerName(entity.getLayerName())
                .status(entity.getStatus())
                .build();
    }

    public static GeorefLayer toEntity(GeorefLayerDto dto, GeorefImage image) {
        GeorefLayer entity = new GeorefLayer();
        entity.setId(dto.getId());
        entity.setImage(image);
        entity.setWorkspace(dto.getWorkspace());
        entity.setStoreName(dto.getStoreName());
        entity.setLayerName(dto.getLayerName());
        entity.setStatus(dto.getStatus());
        return entity;
    }

}
