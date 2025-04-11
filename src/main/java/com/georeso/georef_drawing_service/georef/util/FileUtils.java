package com.georeso.georef_drawing_service.georef.util;

import java.security.MessageDigest;

import org.springframework.web.multipart.MultipartFile;

public class FileUtils {

    public static String calculateSHA256(MultipartFile file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(file.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du calcul du hash du fichier", e);
        }
    }
}
