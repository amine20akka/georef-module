package com.amine.pfe.georef_module.image.service.port;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    public static final List<String> SUPPORTED_MIME_TYPES = List.of(
            "image/png", "image/jpeg", "image/jpg");

    Path exists(String filename);
    Path saveOriginalFile(MultipartFile file, String filename) throws IOException;
    Path getOriginalFilePath(String filename) throws IOException;
    void deleteOriginalFile(String fullPath) throws IOException;
    File getFileByOriginalFilePath(String originalFilePath) throws IOException;
    String removeHashFromFilePath(String filePath) throws IOException;
    MediaType detectMediaType(String filename);
}
