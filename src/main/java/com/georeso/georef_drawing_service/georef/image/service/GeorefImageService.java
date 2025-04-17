package com.georeso.georef_drawing_service.georef.image.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.georeso.georef_drawing_service.georef.entity.GeorefImage;
import com.georeso.georef_drawing_service.georef.exception.ImageNotFoundException;
import com.georeso.georef_drawing_service.georef.image.dto.GeorefImageDto;
import com.georeso.georef_drawing_service.georef.image.exceptions.UnsupportedImageFormatException;
import com.georeso.georef_drawing_service.georef.image.mapper.ImageMapper;
import com.georeso.georef_drawing_service.georef.image.repository.GeorefImageRepository;
import com.georeso.georef_drawing_service.georef.image.service.port.FileStorageService;
import com.georeso.georef_drawing_service.georef.image.service.port.GeorefImageFactory;
import com.georeso.georef_drawing_service.georef.image.service.port.HashCalculator;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class GeorefImageService {

    private final GeorefImageRepository repository;
    private final HashCalculator hashCalculator;
    private final FileStorageService fileStorageService;
    private final GeorefImageFactory imageFactory;

    public GeorefImageDto uploadImage(MultipartFile file) throws IOException {
            String mimeType = file.getContentType();
            if (mimeType == null || !FileStorageService.SUPPORTED_MIME_TYPES.contains(mimeType)) {
                throw new UnsupportedImageFormatException("Format non supporte : " + mimeType);
            }

            String hash = hashCalculator.calculate(file);

            if (repository.existsByHash(hash)) {
                return ImageMapper.toDto(repository.findByHash(hash));
            }

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                throw new IllegalArgumentException("Nom de fichier manquant dans l'image importee.");
            }

            String filename = hash + "_" + originalFilename;
            Path storedPath = fileStorageService.exists(filename);

            if (storedPath == null) {
                storedPath = fileStorageService.saveOriginalFile(file, filename);
            }

            GeorefImage image = imageFactory.create(hash, storedPath);
            GeorefImage saved = repository.save(image);

            return ImageMapper.toDto(saved);

        
    }

    public GeorefImageDto updateGeoreferencingParams(GeorefImageDto dto) {
        UUID imageId = dto.getId();
        GeorefImage image = repository.findById(imageId)
            .orElseThrow(() -> new ImageNotFoundException("Image avec l'ID " + imageId + " non trouvée."));

        image.setTransformationType(dto.getTransformationType());
        image.setSrid(dto.getSrid());
        image.setResamplingMethod(dto.getResamplingMethod());
        image.setCompression(dto.getCompression());

        GeorefImage updated = repository.save(image);
        return ImageMapper.toDto(updated);
    }

    public void deleteImageById(UUID id) {
        GeorefImage image = repository.findById(id)
            .orElseThrow(() -> new ImageNotFoundException("Image introuvable avec l'id " + id));
        repository.delete(image);
    }
}
