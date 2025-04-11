package com.georeso.georef_drawing_service.georef.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.georeso.georef_drawing_service.georef.dto.GeorefImageDto;
import com.georeso.georef_drawing_service.georef.service.GeorefImageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/georef/images")
@RequiredArgsConstructor
@Tag(name = "Georef Image", description = "API pour gérer les images géoréférencées")
public class GeorefImageController {

    private final GeorefImageService imageService;

    @Operation(
        summary = "Importer une image",
        description = "Importe une nouvelle image pour le géoréférencement",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "Image importée avec succès",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = GeorefImageDto.class)
                )
            ),
            @ApiResponse(responseCode = "400", description = "Requête invalide"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
        }
    )
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GeorefImageDto> uploadImage(@RequestParam("file") MultipartFile file) {

        GeorefImageDto response = imageService.uploadImage(file);
        return ResponseEntity.ok(response);
    }
}
