package com.amine.pfe.georef_module.gcp.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amine.pfe.georef_module.entity.Gcp;
import com.amine.pfe.georef_module.entity.GeorefImage;
import com.amine.pfe.georef_module.exception.ImageNotFoundException;
import com.amine.pfe.georef_module.gcp.dto.GcpDto;
import com.amine.pfe.georef_module.gcp.exceptions.DuplicateGcpIndexException;
import com.amine.pfe.georef_module.gcp.exceptions.GcpNotFoundException;
import com.amine.pfe.georef_module.gcp.mapper.GcpMapper;
import com.amine.pfe.georef_module.gcp.repository.GcpRepository;
import com.amine.pfe.georef_module.image.repository.GeorefImageRepository;

import lombok.RequiredArgsConstructor;

@Service
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

        @Transactional
        public List<GcpDto> deleteGcpById(UUID gcpId) {
                Gcp gcpToDelete = gcpRepository.findById(gcpId)
                                .orElseThrow(() -> new GcpNotFoundException("GCP non trouvé avec l'id : " + gcpId));

                UUID imageId = gcpToDelete.getImage().getId();
                int indexToDelete = gcpToDelete.getIndex();

                gcpRepository.delete(gcpToDelete);

                List<Gcp> remainingGcps = gcpRepository.findAllByImageIdOrderByIndex(imageId);

                if (indexToDelete < remainingGcps.size() + 1) {
                        for (int i = 0; i < remainingGcps.size(); i++) {
                                remainingGcps.get(i).setIndex(i + 1);
                        }
                        remainingGcps = gcpRepository.saveAll(remainingGcps);
                }

                return GcpMapper.toGcpDtoList(remainingGcps);
        }

}
