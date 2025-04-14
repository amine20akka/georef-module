package com.georeso.georef_drawing_service.georef.service.port;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface HashCalculator {
    String calculate(MultipartFile file) throws IOException;
}
