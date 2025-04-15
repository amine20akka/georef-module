package com.georeso.georef_drawing_service.georef.gcp.exceptions;

public class InvalidGcpException extends RuntimeException {
    public InvalidGcpException(String message) {
        super(message);
    }

    public InvalidGcpException(String message, Throwable cause) {
        super(message, cause);
    }
}