package com.georeso.georef_drawing_service.georef.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.georeso.georef_drawing_service.georef.dto.GeorefImageDto;
import com.georeso.georef_drawing_service.georef.entity.GeorefImage;
import com.georeso.georef_drawing_service.georef.enums.GeorefStatus;
import com.georeso.georef_drawing_service.georef.exception.UnsupportedImageFormatException;
import com.georeso.georef_drawing_service.georef.repository.GeorefImageRepository;
import com.georeso.georef_drawing_service.georef.service.port.FileStorageService;
import com.georeso.georef_drawing_service.georef.service.port.GeorefImageFactory;
import com.georeso.georef_drawing_service.georef.service.port.HashCalculator;

@ExtendWith(MockitoExtension.class)
class GeorefImageServiceTest {

    @InjectMocks
    private GeorefImageService georefImageService;

    @Mock
    private GeorefImageRepository repository;

    @Mock
    private HashCalculator hashCalculator;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private GeorefImageFactory georefImageFactory;

    // Test image non existante : upload + enregistrement
    @Test
    @DisplayName("doit enregistrer une nouvelle image avec succès")
    void shouldUploadNewImageSuccessfully() throws Exception {
        // GIVEN
        byte[] content = "image content".getBytes(StandardCharsets.UTF_8);
        MultipartFile file = new MockMultipartFile("file", "mock.jpg", "image/png", content);

        String hash = "1a9e46aa05d390aa48745d4bda80a459e850c4b048af0c0faec37d9a2f080abb";
        String filename = hash.concat("_").concat(file.getOriginalFilename());
        UUID imageId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        Path targetPath = Paths.get("storage/originals", filename);

        GeorefImage imageToSave = new GeorefImage();
        imageToSave.setHash(hash);
        imageToSave.setFilepathOriginal(targetPath.toString());
        imageToSave.setUploadingDate(now);
        imageToSave.setStatus(GeorefStatus.UPLOADED);

        GeorefImage savedImage = new GeorefImage();
        savedImage.setId(imageId);
        savedImage.setHash(hash);
        savedImage.setFilepathOriginal(targetPath.toString());
        savedImage.setUploadingDate(now);
        savedImage.setStatus(GeorefStatus.UPLOADED);

        when(hashCalculator.calculate(file)).thenReturn(hash);
        when(repository.existsByHash(hash)).thenReturn(false);
        when(fileStorageService.saveOriginalFile(file, filename)).thenReturn(targetPath);
        when(georefImageFactory.create(hash, targetPath)).thenReturn(imageToSave);
        when(repository.save(imageToSave)).thenReturn(savedImage);

        // WHEN
        GeorefImageDto result = georefImageService.uploadImage(file);

        // THEN
        assertNotNull(result);
        assertEquals(hash, result.getHash());
        assertEquals(imageId, result.getId());
        assertEquals(targetPath.toString(), result.getFilepathOriginal());
        assertEquals(GeorefStatus.UPLOADED, result.getStatus());

        verify(hashCalculator, times(1)).calculate(file);
        verify(fileStorageService, times(1)).saveOriginalFile(file, filename);
        verify(repository, times(1)).save(imageToSave);
    }

    // Test image déjà existante : retourne l'existante sans save
    @Test
    @DisplayName("doit retourner l'image existante sans l'enregistrer à nouveau")
    void shouldReturnExistingImageDto_WhenImageAlreadyExists() throws Exception {
        // GIVEN
        byte[] content = "same image".getBytes(StandardCharsets.UTF_8);
        MultipartFile file = new MockMultipartFile("file", "mock.jpg", "image/png", content);

        String hash = "existing-image-hash";
        UUID imageId = UUID.randomUUID();
        LocalDateTime uploadDate = LocalDateTime.of(2023, 10, 1, 12, 0);
        String path = "originals/" + hash + "_mock.jpg";

        GeorefImage existingImage = new GeorefImage();
        existingImage.setId(imageId);
        existingImage.setHash(hash);
        existingImage.setUploadingDate(uploadDate);
        existingImage.setFilepathOriginal(path);
        existingImage.setStatus(GeorefStatus.UPLOADED);

        when(hashCalculator.calculate(file)).thenReturn(hash);
        when(repository.existsByHash(hash)).thenReturn(true);
        when(repository.findByHash(hash)).thenReturn(existingImage);

        // WHEN
        GeorefImageDto result = georefImageService.uploadImage(file);

        // THEN
        assertNotNull(result);
        assertEquals(imageId, result.getId());
        assertEquals(hash, result.getHash());
        assertEquals(path, result.getFilepathOriginal());
        assertEquals(GeorefStatus.UPLOADED, result.getStatus());
        assertTrue(result.getUploadingDate().isBefore(LocalDateTime.now()));

        verify(hashCalculator, times(1)).calculate(file);
        verify(repository, never()).save(any());
        verify(fileStorageService, never()).saveOriginalFile(any(), any());
        verify(georefImageFactory, never()).create(any(), any());
    }

    // Test pour un type d'image non supporté
    @ParameterizedTest
    @ValueSource(strings = {
            "text/plain",
            "application/pdf",
            "application/octet-stream",
            "application/json",
            "video/mp4",
            "audio/mpeg",
            "image/svg+xml"
    })
    @DisplayName("doit lever une exception UnsupportedImageFormatException pour les types MIME non supportés")
    void shouldThrowUnsupportedImageFormatException_ForUnsupportedMimeTypes(String mimeType) {
        // GIVEN
        byte[] content = "not an image".getBytes(StandardCharsets.UTF_8);
        MultipartFile invalidFile = new MockMultipartFile("file", "test." + mimeType.split("/")[1], mimeType, content);

        // WHEN + THEN
        UnsupportedImageFormatException ex = assertThrows(
                UnsupportedImageFormatException.class,
                () -> georefImageService.uploadImage(invalidFile));

        assertEquals("Format non supporté : " + mimeType, ex.getMessage());

        verifyNoInteractions(hashCalculator, fileStorageService, georefImageFactory, repository);
    }

}