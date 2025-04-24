package com.amine.pfe.georef_module.image.util;

import java.nio.file.Paths;
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

    public static String normalizeOutputFilename(String name, String fallbackBaseName) {
        if (name == null || name.trim().isEmpty()) {
            return fallbackBaseName.replaceAll("\\.", "") + "_georef.tif";
        }

        int lastDotIndex = name.lastIndexOf('.');
        String baseName = (lastDotIndex != -1) ? name.substring(0, lastDotIndex) : name;
    
        String cleanedBaseName = baseName.replaceAll("\\.", "");
    
        return cleanedBaseName + ".tif";
    }

    public static String extractBaseName(String filepathOriginal) {
        String filename = Paths.get(filepathOriginal).getFileName().toString();
        int lastDotIndex = filename.lastIndexOf('.');
        String base = (lastDotIndex != -1) ? filename.substring(0, lastDotIndex) : filename;
        return base.replaceAll("\\.", "");
    }    
    
}
