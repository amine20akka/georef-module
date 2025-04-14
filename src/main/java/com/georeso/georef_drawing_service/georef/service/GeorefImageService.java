package com.georeso.georef_drawing_service.georef.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.georeso.georef_drawing_service.common.exception.ImageUploadException;
import com.georeso.georef_drawing_service.config.StorageConfig;
import com.georeso.georef_drawing_service.georef.dto.GeorefImageDto;
import com.georeso.georef_drawing_service.georef.entity.GeorefImage;
import com.georeso.georef_drawing_service.georef.enums.GeorefStatus;
import com.georeso.georef_drawing_service.georef.mapper.GeorefMapper;
import com.georeso.georef_drawing_service.georef.repository.GeorefImageRepository;
import com.georeso.georef_drawing_service.georef.util.FileUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GeorefImageService {

    private final GeorefImageRepository repository;
    private final StorageConfig storageConfig;

    public GeorefImageDto uploadImage(MultipartFile file) {
        try {
            String hash = FileUtils.calculateSHA256(file);

            // Vérifier si une image avec le même hash existe déjà
            if (repository.existsByHash(hash)) {
                return GeorefMapper.toDto(repository.findByHash(hash), null, null);
            }

            String filename = hash + "_" + file.getOriginalFilename();
            Path targetPath = storageConfig.getOriginalDir().resolve(filename);
            if (!Files.exists(targetPath)) {
                Files.copy(file.getInputStream(), targetPath);
            }

            GeorefImage image = new GeorefImage();
            image.setFilepathOriginal(targetPath.toString());
            image.setHash(hash);
            image.setUploadingDate(LocalDateTime.now());
            image.setStatus(GeorefStatus.UPLOADED);

            GeorefImage savedImage = repository.save(image);
            return GeorefMapper.toDto(savedImage, null, null);

        } catch (IOException e) {
            throw new ImageUploadException("Impossible d'importer l'image", e);
        }
    }

}
