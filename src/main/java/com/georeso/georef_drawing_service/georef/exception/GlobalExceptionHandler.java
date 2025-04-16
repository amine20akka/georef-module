package com.georeso.georef_drawing_service.georef.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.georeso.georef_drawing_service.georef.gcp.exceptions.DuplicateGcpIndexException;
import com.georeso.georef_drawing_service.georef.gcp.exceptions.ImageNotFoundException;
import com.georeso.georef_drawing_service.georef.gcp.exceptions.InvalidGcpException;
import com.georeso.georef_drawing_service.georef.image.exceptions.ImageUploadException;
import com.georeso.georef_drawing_service.georef.image.exceptions.UnsupportedImageFormatException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnsupportedImageFormatException.class)
    public ResponseEntity<String> handleUnsupportedImageFormatException(UnsupportedImageFormatException ex) {
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                             .body("Unsupported image format: " + ex.getMessage());
    }

    @ExceptionHandler(ImageUploadException.class)
    public ResponseEntity<String> handleImageUploadException(ImageUploadException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body("Image upload failed: " + ex.getMessage());
    }

    @ExceptionHandler(ImageNotFoundException.class)
    public ResponseEntity<String> handleImageNotFoundException(ImageNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body("Image not found: " + ex.getMessage());
    }

    @ExceptionHandler(DuplicateGcpIndexException.class)
    public ResponseEntity<String> handleDuplicateGcpIndexException(DuplicateGcpIndexException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .body("Duplicate GCP index: " + ex.getMessage());
    }

    @ExceptionHandler(InvalidGcpException.class)
    public ResponseEntity<String> handleInvalidGcpException(InvalidGcpException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body("Invalid GCP: " + ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body("An error occurred: " + ex.getMessage());
    }
}

