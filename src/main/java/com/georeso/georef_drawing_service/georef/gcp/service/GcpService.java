package com.georeso.georef_drawing_service.georef.gcp.service;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.georeso.georef_drawing_service.georef.entity.Gcp;
import com.georeso.georef_drawing_service.georef.entity.GeorefImage;
import com.georeso.georef_drawing_service.georef.gcp.dto.GcpDto;
import com.georeso.georef_drawing_service.georef.gcp.exceptions.DuplicateGcpIndexException;
import com.georeso.georef_drawing_service.georef.gcp.exceptions.ImageNotFoundException;
import com.georeso.georef_drawing_service.georef.gcp.exceptions.InvalidGcpException;
import com.georeso.georef_drawing_service.georef.gcp.mapper.GcpMapper;
import com.georeso.georef_drawing_service.georef.gcp.repository.GcpRepository;
import com.georeso.georef_drawing_service.georef.image.repository.GeorefImageRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class GcpService {

        private static final Logger log = LoggerFactory.getLogger(GcpService.class);
        private final GcpRepository gcpRepository;
        private final GeorefImageRepository imageRepository;

        public GcpDto addGcp(GcpDto gcpDto) {
                try {
                        UUID imageId = gcpDto.getImageId();
                        if (imageId == null) {
                                log.warn("Tentative d'ajout d'un GCP avec imageId null.");
                                throw new IllegalArgumentException("L'ID de l'image ne peut pas être null.");
                        }

                        GeorefImage image = imageRepository.findById(imageId)
                                        .orElseThrow(() -> {
                                                log.warn("Image introuvable pour l'ID : {}", imageId);
                                                return new ImageNotFoundException(
                                                                "Image avec l'ID " + imageId + " introuvable.");
                                        });

                        if (gcpRepository.existsByImageIdAndIndex(imageId, gcpDto.getIndex())) {
                                log.warn("Un GCP avec le même index existe déjà pour l'image ID : {}", imageId);
                                throw new DuplicateGcpIndexException(
                                                "Un GCP avec ce même index existe déjà pour cette image.");
                        }

                        Gcp gcp = GcpMapper.toEntity(gcpDto, image);
                        Gcp saved = gcpRepository.save(gcp);

                        log.info("GCP ajouté avec succès : ID {}", saved);
                        return GcpMapper.toDto(saved);

                } catch (IllegalArgumentException | ImageNotFoundException | DuplicateGcpIndexException e) {
                        throw e;
                } catch (Exception e) {
                        log.error("Erreur inattendue lors de l'ajout d'un GCP : {}", e.getMessage(), e);
                        throw new InvalidGcpException("Erreur inattendue lors de l'ajout du GCP.", e);
                }
        }

        public List<GcpDto> getGcpsByImageId(UUID imageId) {
                try {
                        if (imageId == null) {
                                log.warn("Tentative de récupération des GCPs avec imageId null.");
                                throw new IllegalArgumentException("L'ID de l'image ne peut pas être null.");
                        }

                        GeorefImage image = imageRepository.findById(imageId)
                                        .orElseThrow(() -> {
                                                log.warn("Image introuvable pour l'ID : {}", imageId);
                                                return new ImageNotFoundException(
                                                                "Image avec l'ID " + imageId + " introuvable.");
                                        });

                        List<Gcp> gcps = gcpRepository.findByImageId(image.getId());

                        log.info("GCPs récupérés avec succès pour l'image ID {} : {} points", imageId, gcps.size());
                        return GcpMapper.toGcpDtoList(gcps);

                } catch (IllegalArgumentException | ImageNotFoundException e) {
                        throw e;
                } catch (Exception e) {
                        log.error("Erreur inattendue lors de la récupération des GCPs : {}", e.getMessage(), e);
                        throw new InvalidGcpException("Erreur inattendue lors de la récupération des GCPs.", e);
                }
        }

}
