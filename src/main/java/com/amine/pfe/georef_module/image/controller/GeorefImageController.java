package com.amine.pfe.georef_module.image.controller;

import java.io.IOException;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.amine.pfe.georef_module.exception.ImageNotFoundException;
import com.amine.pfe.georef_module.image.dto.GeorefImageDto;
import com.amine.pfe.georef_module.image.exceptions.UnsupportedImageFormatException;
import com.amine.pfe.georef_module.image.service.GeorefImageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/georef/image")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Images", description = "API de gestion des images géoréférencées")
public class GeorefImageController {

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

            log.error("Erreur inattendue lors de la mise à jour des paramètres de géoréférencement : {}",
                    e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);

        }
    }

    @Operation(summary = "Supprimer une image", description = "Supprime une image par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Image supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Image non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImage(@PathVariable UUID id) {
        try {

            imageService.deleteImageById(id);
            log.info("Image supprimée avec succès, id={}", id);
            return ResponseEntity.noContent().build();
        
        } catch (ImageNotFoundException ex) {
        
            log.warn("Tentative de suppression d'une image inexistante, id={}", id);
            throw ex;
        
        } catch (Exception ex) {
        
            log.error("Erreur lors de la suppression de l'image, id={}, erreur={}", id, ex.getMessage(), ex);
            throw new RuntimeException("Erreur interne lors de la suppression de l'image");
        
        }
    }

}
