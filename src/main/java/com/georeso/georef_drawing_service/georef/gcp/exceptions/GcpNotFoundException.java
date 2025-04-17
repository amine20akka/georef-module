package com.georeso.georef_drawing_service.georef.gcp.exceptions;

public class GcpNotFoundException extends RuntimeException {
    public GcpNotFoundException(String message) {
        super(message);
    }
}
