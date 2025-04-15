package com.georeso.georef_drawing_service.georef.image.service;

import java.io.IOException;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.georeso.georef_drawing_service.georef.entity.GeorefImage;
import com.georeso.georef_drawing_service.georef.image.dto.GeorefImageDto;
import com.georeso.georef_drawing_service.georef.image.exceptions.ImageUploadException;
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

    private static final Logger log = LoggerFactory.getLogger(GeorefImageService.class);
    private final GeorefImageRepository repository;
    private final HashCalculator hashCalculator;
    private final FileStorageService fileStorageService;
    private final GeorefImageFactory imageFactory;

    public GeorefImageDto uploadImage(MultipartFile file) {
        try {
            String mimeType = file.getContentType();
            if (mimeType == null || !FileStorageService.SUPPORTED_MIME_TYPES.contains(mimeType)) {
                throw new UnsupportedImageFormatException("Format non supporte : " + mimeType);
            }

            String hash = hashCalculator.calculate(file);

            if (repository.existsByHash(hash)) {
                log.info("Image avec hash {} deja existante. Retour de la version existante.", hash);
                return ImageMapper.toDto(repository.findByHash(hash), null, null);
            }

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                log.error("Nom de fichier original introuvable dans MultipartFile.");
                throw new ImageUploadException("Nom de fichier manquant dans l'image importee.");
            }

            String filename = hash + "_" + originalFilename;
            Path storedPath = fileStorageService.exists(filename);

            if (storedPath == null) {
                storedPath = fileStorageService.saveOriginalFile(file, filename);
                log.info("Fichier enregistre sous : {}", storedPath);
            }

            GeorefImage image = imageFactory.create(hash, storedPath);
            GeorefImage saved = repository.save(image);

            log.info("Image {} sauvegardee avec succes dans la base.", saved.getId());
            return ImageMapper.toDto(saved, null, null);

        } catch (UnsupportedImageFormatException e) {
            log.error("Format de fichier non supporté : {}", e.getMessage(), e);
            throw e;
        } catch (IOException e) {
            log.error("Erreur IO lors de l'upload de l'image : {}", e.getMessage(), e);
            throw new ImageUploadException("Erreur lors de l'upload de l'image : " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Erreur inattendue lors de l'importation d'image : {}", e.getMessage(), e);
            throw new ImageUploadException("Erreur inattendue lors de l'importation de l'image", e);
        }
    }

}
