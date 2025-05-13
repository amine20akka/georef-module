package com.amine.pfe.georef_module.image.service.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.amine.pfe.georef_module.image.dto.PublicationResponse;
import com.amine.pfe.georef_module.image.service.port.CartographicServer;
import java.util.Base64;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GeoserverAdapter implements CartographicServer {

    @Value("${geoserver.url}")
    private String GEOSERVER_URL;

    @Value("${geoserver.workspace}")
    private String WORKSPACE;

    @Value("${geoserver.username}")
    private String USER;

    @Value("${geoserver.password}")
    private String PASSWORD;

    private final WebClient webClient;

    public GeoserverAdapter(@Qualifier("geoServerWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Publie une image GeoTIFF sur GeoServer et retourne l'URL WMS de la couche.
     *
     * @param georeferencedImagePath Chemin du fichier GeoTIFF à publier
     * @param layerName              Nom de la couche à publier
     * @param storeName              Nom du store de couverture à créer (peut être
     *                               différent du nom de la couche)
     * @return URL WMS de la couche publiée
     */
    @Override
    public PublicationResponse publishGeoTiff(String georeferencedImagePath, String layerName, String storeName) {
        try {
            // Créer le store de couverture avec le nom du store
            boolean storeCreated = createCoverageStore(storeName, georeferencedImagePath);

            // Publier la couche avec son propre nom
            publishCoverageLayer(storeName, layerName);

            if (storeCreated) {
                String wmsUrl = generateWmsUrl(layerName);
                PublicationResponse response = new PublicationResponse(
                        WORKSPACE,
                        storeName,
                        layerName,
                        wmsUrl);
                return response;
            } else {
                throw new RuntimeException("Echec de la publication sur GeoServer");
            }
        } catch (Exception e) {
            log.error("Erreur lors de la publication sur GeoServer", e);
            throw new RuntimeException("Erreur de publication GeoServer", e);
        }
    }

    /**
     * Crée un store de couverture dans GeoServer.
     *
     * @param storeName Nom du store de couverture
     * @param filePath  Chemin du fichier GeoTIFF
     * @return true si le store est créé avec succès
     */
    private boolean createCoverageStore(String storeName, String filePath) {
        String url = UriComponentsBuilder.fromUriString(GEOSERVER_URL)
                .path("/rest/workspaces/{workspace}/coveragestores")
                .buildAndExpand(WORKSPACE)
                .toUriString();

        // Préparer le payload XML pour créer le store
        String payload = String.format(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                        "<coverageStore>" +
                        "  <name>%s</name>" +
                        "  <type>GeoTIFF</type>" +
                        "  <enabled>true</enabled>" +
                        "  <url>file:C:/Users/aakkari/Downloads/pfe-sig-platform/%s</url>" +
                        "  <workspace>" +
                        "    <name>%s</name>" +
                        "  </workspace>" +
                        "</coverageStore>",
                storeName, filePath, WORKSPACE);

        HttpHeaders headers = createAuthHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);

        try {
            ResponseEntity<String> response = webClient.post()
                    .uri(url)
                    .headers(httpHeaders -> httpHeaders.addAll(headers))
                    .bodyValue(payload)
                    .retrieve()
                    .toEntity(String.class)
                    .block();

            return response != null && response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.error("Erreur lors de la création du store de couverture", e);
            return false;
        }
    }

    /**
     * Publie la couche de couverture dans GeoServer avec un nom spécifique.
     *
     * @param storeName Nom du store de couverture
     * @param layerName Nom de la couche à publier
     * @return true si la couche est publiée avec succès
     */
    private boolean publishCoverageLayer(String storeName, String layerName) {
        String url = UriComponentsBuilder.fromUriString(GEOSERVER_URL)
                .path("/rest/workspaces/{workspace}/coveragestores/{storeName}/coverages")
                .buildAndExpand(WORKSPACE, storeName)
                .toUriString();

        // Préparer le payload XML pour publier la couche
        String payload = String.format(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                        "<coverage>" +
                        "  <name>%s</name>" +
                        "  <enabled>true</enabled>" +
                        "</coverage>",
                layerName);

        HttpHeaders headers = createAuthHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);

        try {
            String response = webClient.post()
                    .uri(url)
                    .headers(httpHeaders -> httpHeaders.addAll(headers))
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return response != null;
        } catch (Exception e) {
            log.error("Erreur lors de la publication de la couche", e);
            return false;
        }
    }

    /**
     * Génère l'URL WMS pour la couche publiée.
     *
     * @param layerName Nom de la couche
     * @return URL WMS de la couche
     */
    private String generateWmsUrl(String layerName) {
        return UriComponentsBuilder.fromUriString(GEOSERVER_URL)
                .path("/ows")
                .queryParam("service", "WMS")
                .queryParam("version", "1.3.0")
                .queryParam("request", "GetCapabilities")
                .queryParam("layers", WORKSPACE + ":" + layerName)
                .build()
                .toUriString();
    }

    /**
     * Crée les en-têtes d'authentification pour les requêtes REST.
     *
     * @return HttpHeaders avec authentification Basic
     */
    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String credentials = USER + ":" + PASSWORD;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        headers.add("Authorization", "Basic " + encodedCredentials);
        return headers;
    }

    /**
     * Supprime une couche et son store de couverture associé sur GeoServer.
     *
     * @param layerName Nom de la couche à supprimer
     * @return true si la suppression est réussie, false sinon
     */
    @Override
    public boolean deleteGeoTiffLayer(String layerName, String storeName) {
        try {
            // Étape 1: Supprimer la couche
            boolean layerDeleted = deleteCoverage(layerName, storeName);

            // Étape 2: Si la couche est supprimée, supprimer aussi le store de couverture
            boolean storeDeleted = false;
            if (layerDeleted) {
                storeDeleted = deleteCoverageStore(storeName);
            }

            // Retourner vrai si les deux opérations ont réussi
            return layerDeleted && storeDeleted;
        } catch (Exception e) {
            log.error("Erreur lors de la suppression de la couche et du store sur GeoServer", e);
            return false;
        }
    }

    /**
     * Supprime une couche (coverage) de GeoServer.
     *
     * @param layerName Nom de la couche à supprimer
     * @return true si la suppression est réussie
     */
    private boolean deleteCoverage(String layerName, String storeName) {
        String url = UriComponentsBuilder.fromUriString(GEOSERVER_URL)
                .path("/rest/workspaces/{workspace}/coveragestores/{storeName}/coverages/{coverageName}")
                .queryParam("recurse", "true") // Supprimer les ressources associées
                .buildAndExpand(WORKSPACE, storeName, layerName)
                .toUriString();

        HttpHeaders headers = createAuthHeaders();

        try {
            ResponseEntity<String> response = webClient.delete()
                    .uri(url)
                    .headers(httpHeaders -> httpHeaders.addAll(headers))
                    .retrieve()
                    .toEntity(String.class)
                    .block();

            return response != null && response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.error("Erreur lors de la suppression de la couche", e);
            return false;
        }
    }

    /**
     * Supprime un store de couverture de GeoServer.
     *
     * @param storeName Nom du store à supprimer
     * @return true si la suppression est réussie
     */
    private boolean deleteCoverageStore(String storeName) {
        String url = UriComponentsBuilder.fromUriString(GEOSERVER_URL)
                .path("/rest/workspaces/{workspace}/coveragestores/{storeName}")
                .queryParam("recurse", "true") // Supprimer les ressources associées
                .buildAndExpand(WORKSPACE, storeName)
                .toUriString();

        HttpHeaders headers = createAuthHeaders();

        try {
            ResponseEntity<String> response = webClient.delete()
                    .uri(url)
                    .headers(httpHeaders -> httpHeaders.addAll(headers))
                    .retrieve()
                    .toEntity(String.class)
                    .block();

            return response != null && response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.error("Erreur lors de la suppression du store de couverture", e);
            return false;
        }
    }
}
