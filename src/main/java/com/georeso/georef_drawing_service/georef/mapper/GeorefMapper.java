package com.georeso.georef_drawing_service.georef.mapper;

import com.georeso.georef_drawing_service.georef.dto.*;
import com.georeso.georef_drawing_service.georef.entity.*;

import java.util.List;
import java.util.stream.Collectors;

public class GeorefMapper {

    // -------- GeorefImage --------
    public static GeorefImageDto toDto(GeorefImage entity, List<GcpDto> gcpDtos, GeorefLayerDto layerDto) {
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
                .gcps(gcpDtos)
                .layer(layerDto)
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

    // -------- GCP --------
    public static GcpDto toDto(Gcp entity) {
        return GcpDto.builder()
                .id(entity.getId())
                .imageId(entity.getImage().getId())
                .sourceX(entity.getSourceX())
                .sourceY(entity.getSourceY())
                .mapX(entity.getMapX())
                .mapY(entity.getMapY())
                .index(entity.getIndex())
                .color(entity.getColor())
                .residual(entity.getResidual())
                .build();
    }

    public static Gcp toEntity(GcpDto dto, GeorefImage image) {
        Gcp entity = new Gcp();
        entity.setId(dto.getId());
        entity.setImage(image);
        entity.setSourceX(dto.getSourceX());
        entity.setSourceY(dto.getSourceY());
        entity.setMapX(dto.getMapX());
        entity.setMapY(dto.getMapY());
        entity.setIndex(dto.getIndex());
        entity.setColor(dto.getColor());
        entity.setResidual(dto.getResidual());
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

    // -------- Util --------
    public static List<GcpDto> toGcpDtoList(List<Gcp> gcps) {
        return gcps.stream().map(GeorefMapper::toDto).collect(Collectors.toList());
    }
}
