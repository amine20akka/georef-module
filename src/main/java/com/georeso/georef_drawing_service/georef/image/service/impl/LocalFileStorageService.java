package com.georeso.georef_drawing_service.georef.image.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.georeso.georef_drawing_service.config.StorageConfig;
import com.georeso.georef_drawing_service.georef.image.service.port.FileStorageService;

@Component
public class LocalFileStorageService implements FileStorageService {

    private final StorageConfig storageConfig;

    public LocalFileStorageService(StorageConfig storageConfig) {
        this.storageConfig = storageConfig;
    }

    @Override
    public Path exists(String filename) {
        Path targetPath = storageConfig.getOriginalDir().resolve(filename);
        if (Files.exists(targetPath)) {
            return targetPath;
        } else {
            return null;
        }
    }

    @Override
    public Path saveOriginalFile(MultipartFile file, String filename) throws IOException {
        Path targetPath = storageConfig.getOriginalDir().resolve(filename);
        if (!Files.exists(targetPath)) {
            Files.copy(file.getInputStream(), targetPath);
        }
        return targetPath;
    }
}
