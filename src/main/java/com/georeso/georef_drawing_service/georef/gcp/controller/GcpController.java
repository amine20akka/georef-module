package com.georeso.georef_drawing_service.georef.gcp.controller;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.georeso.georef_drawing_service.georef.gcp.dto.GcpDto;
import com.georeso.georef_drawing_service.georef.gcp.service.GcpService;

@RestController
@RequestMapping("/georef/gcp")
@RequiredArgsConstructor
public class GcpController {

    private final GcpService gcpService;

    @PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GcpDto> addGcp(@RequestBody GcpDto request) {
        GcpDto response = gcpService.addGcp(request);
        return ResponseEntity.status(200).body(response);
    }
    @GetMapping(value = "/get/{imageId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GcpDto>> getGcpsByImageId(@PathVariable UUID imageId) {
        List<GcpDto> gcps = gcpService.getGcpsByImageId(imageId);
        return ResponseEntity.status(200).body(gcps);
    }
}