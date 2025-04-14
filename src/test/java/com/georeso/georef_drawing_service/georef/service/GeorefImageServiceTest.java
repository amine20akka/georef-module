package com.georeso.georef_drawing_service.georef.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.georeso.georef_drawing_service.config.StorageConfig;
import com.georeso.georef_drawing_service.georef.dto.GeorefImageDto;
import com.georeso.georef_drawing_service.georef.entity.GeorefImage;
import com.georeso.georef_drawing_service.georef.enums.GeorefStatus;
import com.georeso.georef_drawing_service.georef.repository.GeorefImageRepository;
import com.georeso.georef_drawing_service.georef.util.FileUtils;

@ExtendWith(MockitoExtension.class)
class GeorefImageServiceTest {

    @InjectMocks
    private GeorefImageService georefImageService;

    @Mock
    private GeorefImageRepository repository;

    @Mock
    private StorageConfig storageConfig;

    // Test image non existante : upload + enregistrement
    @Test
    public void shouldUploadNewImageSuccessfully() throws Exception {
        // GIVEN
        byte[] content = "image content".getBytes(StandardCharsets.UTF_8);
        MultipartFile file = new MockMultipartFile("file", "mock.jpg", "image/png", content);

        String hash = "1a9e46aa05d390aa48745d4bda80a459e850c4b048af0c0faec37d9a2f080abb";
        UUID imageId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        // Simuler le dossier original
        Path originalDir = Paths.get("storage/originals");
        Path expectedFilePath = originalDir.resolve(hash + "_mock.jpg");

        GeorefImage savedImage = new GeorefImage();
        savedImage.setId(imageId);
        savedImage.setHash(hash);
        savedImage.setFilepathOriginal(expectedFilePath.toString());
        savedImage.setUploadingDate(now);
        savedImage.setStatus(GeorefStatus.UPLOADED);

        // Simule que le hash est inconnu => nouvelle image
        when(repository.existsByHash(hash)).thenReturn(false);
        when(storageConfig.getOriginalDir()).thenReturn(originalDir);
        when(repository.save(any(GeorefImage.class))).thenAnswer(invocation -> {
            GeorefImage img = invocation.getArgument(0);
            img.setId(imageId);
            img.setUploadingDate(now);
            return img;
        });

        try (MockedStatic<FileUtils> fileUtilsMock = mockStatic(FileUtils.class)) {
            fileUtilsMock.when(() -> FileUtils.calculateSHA256(file)).thenReturn(hash);

            // WHEN
            GeorefImageDto result = georefImageService.uploadImage(file);

            // THEN
            assertNotNull(result);
            assertEquals(hash, result.getHash());
            assertEquals(imageId, result.getId());
            assertEquals(result.getFilepathOriginal(), "storage\\originals\\" + hash + "_mock.jpg");
            assertEquals(GeorefStatus.UPLOADED, result.getStatus());

            // Vérifications des interactions
            fileUtilsMock.verify(() -> FileUtils.calculateSHA256(file), times(1));
            verify(repository, times(1)).save(any(GeorefImage.class));
        }
    }

    // Test image déjà existante : retourner l'existante
    @Test
    public void shouldReturnExistingImageDto_WhenImageAlreadyExists() throws Exception {
        // GIVEN
        byte[] content = "same image content".getBytes(StandardCharsets.UTF_8);
        MultipartFile file = new MockMultipartFile("file", "mock.jpg", "image/png", content);

        String existingHash = "1a9e46aa05d390aa48745d4bda80a459e850c4b048af0c0faec37d9a2f080abb";
        UUID existingId = UUID.randomUUID();
        LocalDateTime uploadDate = LocalDateTime.of(2023, 10, 1, 12, 0);

        GeorefImage existingImage = new GeorefImage();
        existingImage.setId(existingId);
        existingImage.setHash(existingHash);
        existingImage.setFilepathOriginal("originals/" + existingHash + "_mock.jpg");
        existingImage.setUploadingDate(uploadDate);
        existingImage.setStatus(GeorefStatus.UPLOADED);

        when(repository.existsByHash(existingHash)).thenReturn(true);
        when(repository.findByHash(existingHash)).thenReturn(existingImage);

        try (MockedStatic<FileUtils> fileUtilsMock = mockStatic(FileUtils.class)) {
            fileUtilsMock.when(() -> FileUtils.calculateSHA256(file)).thenReturn(existingHash);

            // WHEN
            GeorefImageDto result = georefImageService.uploadImage(file);

            // THEN
            assertNotNull(result);
            assertEquals(existingId, result.getId());
            assertEquals(existingHash, result.getHash());
            assertEquals("originals/" + existingHash + "_mock.jpg", result.getFilepathOriginal());
            assertEquals(GeorefStatus.UPLOADED, result.getStatus());

            // Vérifie que l'image n'est pas enregistrée à nouveau
            verify(repository, never()).save(any());
            // Vérifie que le hash est bien calculé
            fileUtilsMock.verify(() -> FileUtils.calculateSHA256(file), times(1));
        }
    }

    // Test erreur IO
    @Test
    public void shouldThrowImageUploadException_WhenIOExceptionOccurs() throws Exception {
        // GIVEN
        MultipartFile file = mock(MultipartFile.class);

        when(file.getBytes()).thenThrow(new RuntimeException("Fail reading file"));

        try (MockedStatic<FileUtils> fileUtilsMock = mockStatic(FileUtils.class)) {
            fileUtilsMock.when(() -> FileUtils.calculateSHA256(file)).thenThrow(new RuntimeException("Erreur SHA256"));

            // WHEN + THEN
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> georefImageService.uploadImage(file));
            assertTrue(exception.getMessage().contains("Erreur lors du calcul du hash du fichier"));
        }
    }
}