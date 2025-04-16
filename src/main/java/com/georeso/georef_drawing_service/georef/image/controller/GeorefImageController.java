package com.georeso.georef_drawing_service.georef.image.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.georeso.georef_drawing_service.georef.gcp.exceptions.ImageNotFoundException;
import com.georeso.georef_drawing_service.georef.image.dto.GeorefImageDto;
import com.georeso.georef_drawing_service.georef.image.exceptions.UnsupportedImageFormatException;
import com.georeso.georef_drawing_service.georef.image.service.GeorefImageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/georef/image")
@RequiredArgsConstructor
@Tag(name = "GeorefImageController", description = "Controller pour la gestion des images géoréférencées")
public class GeorefImageController {

    private static final Logger log = LoggerFactory.getLogger(GeorefImageService.class);
    private final GeorefImageService imageService;

    @Operation(summary = "Importer une image raster", description = "Permet d'importer une image à géoréférencer. Le fichier doit être au format PNG, JPEG ou TIFF.", responses = {
            @ApiResponse(responseCode = "200", description = "Image importée avec succès", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GeorefImageDto.class))),
            @ApiResponse(responseCode = "415", description = "Format de fichier non supporté", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GeorefImageDto.class))),
            @ApiResponse(responseCode = "500", description = "Erreur interne lors de l'importation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GeorefImageDto.class))),
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GeorefImageDto> uploadImage(@RequestParam("file") MultipartFile file) {
        try {

            GeorefImageDto imageDto = imageService.uploadImage(file);
            log.info("Image importée avec succès : {}", imageDto);
            return ResponseEntity.status(200).body(imageDto);
        
        } catch (UnsupportedImageFormatException e) {

            log.error("Format de fichier non supporté : {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(null);
        
        } catch (IOException e) {

            log.error("Erreur IO lors de l'upload de l'image : {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        
        } catch (IllegalArgumentException e) {
            
            log.error("Erreur lors de l'upload de l'image : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(null);
        
        } catch (Exception e) {
        
            log.error("Erreur inattendue lors de l'importation d'image : {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        
        }
    }

    @Operation(summary = "Mettre à jour les paramètres de géoréférencement", description = "Met à jour les paramètres de géoréférencement d'une image existante.", responses = {
            @ApiResponse(responseCode = "200", description = "Paramètres de géoréférencement mis à jour avec succès", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GeorefImageDto.class))),
            @ApiResponse(responseCode = "404", description = "Image non trouvée", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GeorefImageDto.class))),
            @ApiResponse(responseCode = "500", description = "Erreur interne lors de la mise à jour des paramètres", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GeorefImageDto.class))),
    })
    @PutMapping("/georef-params")
    public ResponseEntity<GeorefImageDto> updateGeorefParams(@RequestBody GeorefImageDto georefImageDto) {
        try {
            
            GeorefImageDto updated = imageService.updateGeoreferencingParams(georefImageDto);
            log.info("Paramètres de géoréférencement mis à jour avec succès");
            return ResponseEntity.status(200).body(updated);
        
        } catch (ImageNotFoundException e) {

            log.error("Image non trouvée : {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        
        } catch (Exception e) {

            log.error("Erreur inattendue lors de la mise à jour des paramètres de géoréférencement : {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        
        }
    }
}
