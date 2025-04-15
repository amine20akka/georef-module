package com.georeso.georef_drawing_service.georef.image.service.port;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    public static final List<String> SUPPORTED_MIME_TYPES = List.of(
            "image/png", "image/jpeg", "image/jpg");

    Path exists(String filename);
    Path saveOriginalFile(MultipartFile file, String filename) throws IOException;
}
