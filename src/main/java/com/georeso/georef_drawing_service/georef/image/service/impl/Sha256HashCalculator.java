package com.georeso.georef_drawing_service.georef.image.service.impl;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.georeso.georef_drawing_service.georef.image.service.port.HashCalculator;
import com.georeso.georef_drawing_service.georef.image.util.FileUtils;

@Component
public class Sha256HashCalculator implements HashCalculator {
    @Override
    public String calculate(MultipartFile file) throws IOException {
        return FileUtils.calculateSHA256(file);
    }
}
