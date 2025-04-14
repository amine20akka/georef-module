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
@Tag(name = "GeorefImageController", description = "Controller pour la gestion des images géoréférencées")
public class GeorefImageController {

    private final GeorefImageService imageService;

    @Operation(summary = "Importer une image raster", description = "Permet d'importer une image à géoréférencer. Le fichier doit être au format PNG, JPEG ou TIFF.", responses = {
            @ApiResponse(responseCode = "200", description = "Image importée avec succès", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GeorefImageDto.class))),
            @ApiResponse(responseCode = "415", description = "Format de fichier non supporté", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GeorefImageDto.class))),
            @ApiResponse(responseCode = "500", description = "Erreur interne lors de l'importation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GeorefImageDto.class))),
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GeorefImageDto> uploadImage(@RequestParam("file") MultipartFile file) {
        GeorefImageDto imageDto = imageService.uploadImage(file);
        return ResponseEntity.ok(imageDto);
    }
}
