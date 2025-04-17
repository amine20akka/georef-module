package com.georeso.georef_drawing_service.georef.gcp.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.georeso.georef_drawing_service.georef.entity.Gcp;
import com.georeso.georef_drawing_service.georef.entity.GeorefImage;
import com.georeso.georef_drawing_service.georef.exception.ImageNotFoundException;
import com.georeso.georef_drawing_service.georef.gcp.dto.GcpDto;
import com.georeso.georef_drawing_service.georef.gcp.exceptions.DuplicateGcpIndexException;
import com.georeso.georef_drawing_service.georef.gcp.mapper.GcpMapper;
import com.georeso.georef_drawing_service.georef.gcp.repository.GcpRepository;
import com.georeso.georef_drawing_service.georef.image.repository.GeorefImageRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class GcpService {

        private final GcpRepository gcpRepository;
        private final GeorefImageRepository imageRepository;

        public GcpDto addGcp(GcpDto gcpDto) {

                UUID imageId = gcpDto.getImageId();
                if (imageId == null) {
                        throw new IllegalArgumentException("L'ID de l'image ne peut pas être null.");
                }

                GeorefImage image = imageRepository.findById(imageId)
                                .orElseThrow(() -> {
                                        return new ImageNotFoundException(
                                                        "Image avec l'ID " + imageId + " introuvable.");
                                });

                if (gcpRepository.existsByImageIdAndIndex(imageId, gcpDto.getIndex())) {
                        throw new DuplicateGcpIndexException("Un GCP avec ce même index existe déjà pour cette image.");
                }

                Gcp gcp = GcpMapper.toEntity(gcpDto, image);
                Gcp saved = gcpRepository.save(gcp);

                return GcpMapper.toDto(saved);

        }

        public List<GcpDto> getGcpsByImageId(UUID imageId) {

                if (imageId == null) {
                        throw new IllegalArgumentException("L'ID de l'image ne peut pas être null.");
                }

                GeorefImage image = imageRepository.findById(imageId)
                                .orElseThrow(() -> {
                                        return new ImageNotFoundException(
                                                        "Image avec l'ID " + imageId + " introuvable.");
                                });

                List<Gcp> gcps = gcpRepository.findByImageId(image.getId());
                return GcpMapper.toGcpDtoList(gcps);

        }

}
