package com.amine.pfe.georef_module.image.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amine.pfe.georef_module.config.StorageConfig;
import com.amine.pfe.georef_module.exception.ImageNotFoundException;
import com.amine.pfe.georef_module.image.service.port.FileStorageService;

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

    @Override
    public Path getOriginalFilePath(String filename) throws IOException {
        Path targetPath = storageConfig.getOriginalDir().resolve(filename);
        if (!Files.exists(targetPath)) {
            throw new IOException("File not found: " + filename);
        }
        return targetPath;
    }

    @Override
    public void deleteOriginalFile(String fullPath) throws IOException {
        Path targetPath = Paths.get(fullPath);
        if (Files.exists(targetPath)) {
            Files.delete(targetPath);
        } else {
            throw new ImageNotFoundException("File not found: " + fullPath);
        }
    }
}
