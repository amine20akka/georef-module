package com.amine.pfe.georef_module.gcp.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.amine.pfe.georef_module.exception.ImageNotFoundException;
import com.amine.pfe.georef_module.gcp.dto.GcpDto;
import com.amine.pfe.georef_module.gcp.exceptions.DuplicateGcpIndexException;
import com.amine.pfe.georef_module.gcp.exceptions.GcpNotFoundException;
import com.amine.pfe.georef_module.gcp.service.GcpService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/georef/gcp")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "GCP", description = "API pour la gestion des Ground Control Points")
public class GcpController {

    private final GcpService gcpService;

    @Operation(summary = "Ajouter un GCP", description = "Permet d'ajouter un GCP à une image géoréférencée.", responses = {
            @ApiResponse(responseCode = "200", description = "GCP ajouté avec succès"),
            @ApiResponse(responseCode = "400", description = "Erreur de validation des données d'entrée"),
            @ApiResponse(responseCode = "404", description = "Image introuvable"),
            @ApiResponse(responseCode = "409", description = "Erreur de doublon d'index GCP"),
            @ApiResponse(responseCode = "500", description = "Erreur inattendue lors de l'ajout d'un GCP")
    })
    @PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GcpDto> addGcp(@RequestBody GcpDto gcpDto) {
        try {

            GcpDto gcpDtoResponse = gcpService.addGcp(gcpDto);
            log.info("GCP ajouté avec succès : {}", gcpDtoResponse);
            return ResponseEntity.status(200).body(gcpDtoResponse);

        } catch (IllegalArgumentException e) {

            log.error("Erreur de validation des données d'entrée : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(null);

        } catch (ImageNotFoundException e) {

            log.error("Image introuvable : {}", e.getMessage(), e);
            return ResponseEntity.status(404).body(null);

        } catch (DuplicateGcpIndexException e) {

            log.error("Erreur de doublon d'index GCP : {}", e.getMessage(), e);
            return ResponseEntity.status(409).body(null);

        } catch (Exception e) {

            log.error("Erreur inattendue lors de l'ajout d'un GCP : {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(null);

        }
    }

    @Operation(summary = "Récupérer les GCPs par ID d'image", description = "Permet de récupérer tous les GCPs associés à une image géoréférencée.", responses = {
            @ApiResponse(responseCode = "200", description = "Liste des GCPs récupérée avec succès"),
            @ApiResponse(responseCode = "400", description = "Erreur de validation des données d'entrée"),
            @ApiResponse(responseCode = "404", description = "Image introuvable"),
            @ApiResponse(responseCode = "500", description = "Erreur inattendue lors de la récupération des GCPs")
    })
    @GetMapping(value = "/get/{imageId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GcpDto>> getGcpsByImageId(@PathVariable UUID imageId) {
        try {

            List<GcpDto> gcps = gcpService.getGcpsByImageId(imageId);
            log.info("Liste des GCPs récupérée avec succès pour l'image ID {} : {}", imageId, gcps);
            return ResponseEntity.status(200).body(gcps);

        } catch (IllegalArgumentException e) {

            log.error("Erreur de validation des données d'entrée : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(null);

        } catch (ImageNotFoundException e) {
            
            log.error("Image introuvable : {}", e.getMessage(), e);
            return ResponseEntity.status(404).body(null);
        
        } catch (Exception e) {

            log.error("Erreur inattendue lors de la récupération des GCPs : {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(null);
        
        }
    }

    @Operation(summary = "Supprimer un GCP par ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "GCP supprimé avec succès"),
        @ApiResponse(responseCode = "404", description = "GCP non trouvé", content = @Content),
        @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GcpDto>> deleteGcpById(@PathVariable UUID id) {
        try {
        
            List<GcpDto> updatedGcpDtos = gcpService.deleteGcpById(id);
            log.info("GCP supprimé avec succès : {}", id);
            return ResponseEntity.status(200).body(updatedGcpDtos);
        
        } catch (GcpNotFoundException e) {
        
            log.error("Erreur lors de la suppression du GCP : {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        
        } catch (Exception e) {
        
            log.error("Erreur inattendue lors de la suppression du GCP : {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        
        }
    }

}