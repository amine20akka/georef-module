package com.amine.pfe.georef_module.gcp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.UUID;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.amine.pfe.georef_module.entity.Gcp;
import com.amine.pfe.georef_module.entity.GeorefImage;
import com.amine.pfe.georef_module.exception.ImageNotFoundException;
import com.amine.pfe.georef_module.gcp.dto.GcpDto;
import com.amine.pfe.georef_module.gcp.exceptions.DuplicateGcpIndexException;
import com.amine.pfe.georef_module.gcp.exceptions.GcpNotFoundException;
import com.amine.pfe.georef_module.gcp.mapper.GcpMapper;
import com.amine.pfe.georef_module.gcp.repository.GcpRepository;
import com.amine.pfe.georef_module.image.repository.GeorefImageRepository;

@ExtendWith(MockitoExtension.class)
class GcpServiceTest {

    @Mock
    private GcpRepository gcpRepository;

    @Mock
    private GeorefImageRepository imageRepository;

    @InjectMocks
    private GcpService gcpService;

    @Test
    void shouldAddGcpSuccessfully() {
        // Given
        UUID imageId = UUID.randomUUID();

        GcpDto addGcpRequest = GcpDto.builder()
                .imageId(imageId)
                .sourceX(10)
                .sourceY(20)
                .mapX(100.0)
                .mapY(200.0)
                .index(2)
                .residual(0.5)
                .build();

        GeorefImage image = new GeorefImage();
        Gcp savedGcp = GcpMapper.toEntity(addGcpRequest, image);

        when(imageRepository.findById(imageId)).thenReturn(Optional.of(image));
        when(gcpRepository.save(any(Gcp.class))).thenReturn(savedGcp);

        // When
        GcpDto gcpDto = gcpService.addGcp(addGcpRequest);

        // Then
        assertNotNull(gcpDto);
        assertEquals(savedGcp.getId(), gcpDto.getId());
        verify(imageRepository, times(1)).findById(imageId);
        verify(gcpRepository, times(1)).save(any(Gcp.class));
    }

    @Test
    void shouldThrowWhenImageIdIsNull() {
        // Given
        GcpDto addGcpRequest = GcpDto.builder().imageId(null).build();

        // When + Then
        assertThrows(IllegalArgumentException.class, () -> gcpService.addGcp(addGcpRequest));
        assertThrows(IllegalArgumentException.class, () -> gcpService.getGcpsByImageId(null));
        verifyNoInteractions(imageRepository);
        verifyNoInteractions(gcpRepository);
    }

    @Test
    void shouldThrowWhenImageNotFound() {
        // Given
        UUID imageId = UUID.randomUUID();
        GcpDto addGcpRequest = GcpDto.builder().imageId(imageId).build();

        when(imageRepository.findById(imageId)).thenReturn(Optional.empty());

        // When + Then
        assertThrows(ImageNotFoundException.class, () -> gcpService.addGcp(addGcpRequest));
        verify(imageRepository, times(1)).findById(imageId);
        verifyNoInteractions(gcpRepository);
    }

    @Test
    void shouldThrowWhenDuplicateIndexByImage() {
        // Given
        UUID imageId = UUID.randomUUID();
        GeorefImage image = new GeorefImage();
        GcpDto addGcpRequest = GcpDto.builder().imageId(imageId).index(1).build();

        when(imageRepository.findById(imageId)).thenReturn(Optional.of(image));
        when(gcpRepository.existsByImageIdAndIndex(imageId, 1)).thenReturn(true);

        // When + Then
        assertThrows(DuplicateGcpIndexException.class, () -> gcpService.addGcp(addGcpRequest));
        verify(gcpRepository, never()).save(any(Gcp.class));
    }

    @Test
    void shouldGetGcpsByImageIdSuccessfully() {
        // Given
        GeorefImage image = new GeorefImage();
        image.setId(UUID.randomUUID());

        Gcp gcp = new Gcp();
        gcp.setId(UUID.randomUUID());
        gcp.setImage(image);

        when(imageRepository.findById(image.getId())).thenReturn(Optional.of(image));
        when(gcpRepository.findByImageId(image.getId())).thenReturn(List.of(gcp));

        // When
        List<GcpDto> gcps = gcpService.getGcpsByImageId(image.getId());

        // Then
        assertNotNull(gcps);
        assertEquals(1, gcps.size());
        verify(gcpRepository, times(1)).findByImageId(image.getId());
    }

    @Test
    @DisplayName("doit supprimer le dernier GCP sans réindexer les GCPs restants")
    void shouldDeleteGcpWithoutReindexing_WhenDeletingLastGcp() {
        // GIVEN
        UUID imageId = UUID.randomUUID();
        UUID gcpIdToDelete = UUID.randomUUID();

        GeorefImage image = new GeorefImage();
        image.setId(imageId);

        Gcp gcpToDelete = new Gcp();
        gcpToDelete.setId(gcpIdToDelete);
        gcpToDelete.setImage(image);
        gcpToDelete.setIndex(3);

        Gcp gcp1 = new Gcp();
        gcp1.setId(UUID.randomUUID());
        gcp1.setImage(image);
        gcp1.setIndex(1);
        Gcp gcp2 = new Gcp();
        gcp2.setId(UUID.randomUUID());
        gcp2.setImage(image);
        gcp2.setIndex(2);

        image.setGcps(List.of(gcp1, gcp2, gcpToDelete));

        when(gcpRepository.findById(gcpIdToDelete)).thenReturn(Optional.of(gcpToDelete));
        when(gcpRepository.findAllByImageIdOrderByIndex(imageId)).thenReturn(List.of(gcp1, gcp2));

        // WHEN
        gcpService.deleteGcpById(gcpIdToDelete);

        // THEN
        verify(gcpRepository, times(1)).delete(gcpToDelete);
        verify(gcpRepository, never()).saveAll(anyList());

        assertEquals(1, gcp1.getIndex());
        assertEquals(2, gcp2.getIndex());
    }

    @Test
    @DisplayName("doit supprimer le GCP et réindexer les GCPs restants")
    void shouldDeleteGcpAndReindexing_WhenDeletingGcpIsNotLast() {
        // GIVEN
        UUID imageId = UUID.randomUUID();
        UUID gcpIdToDelete = UUID.randomUUID();

        GeorefImage image = new GeorefImage();
        image.setId(imageId);

        Gcp gcpToDelete = new Gcp();
        gcpToDelete.setId(gcpIdToDelete);
        gcpToDelete.setImage(image);
        gcpToDelete.setIndex(2);

        Gcp gcp1 = new Gcp();
        gcp1.setId(UUID.randomUUID());
        gcp1.setImage(image);
        gcp1.setIndex(1);
        Gcp gcp3 = new Gcp();
        gcp3.setId(UUID.randomUUID());
        gcp3.setImage(image);
        gcp3.setIndex(3);

        image.setGcps(List.of(gcp1, gcp3, gcpToDelete));

        when(gcpRepository.findById(gcpIdToDelete)).thenReturn(Optional.of(gcpToDelete));
        when(gcpRepository.findAllByImageIdOrderByIndex(imageId)).thenReturn(List.of(gcp1, gcp3));

        // WHEN
        gcpService.deleteGcpById(gcpIdToDelete);

        // THEN
        verify(gcpRepository, times(1)).delete(gcpToDelete);
        verify(gcpRepository, times(1)).saveAll(anyList());

        assertEquals(1, gcp1.getIndex());
        assertEquals(2, gcp3.getIndex());
    }

    @Test
    @DisplayName("doit lever GcpNotFoundException si GCP non trouvé")
    void shouldThrowExceptionWhenGcpNotFound() {
        // GIVEN
        UUID id = UUID.randomUUID();
        when(gcpRepository.findById(id)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThrows(GcpNotFoundException.class, () -> gcpService.deleteGcpById(id));
        verify(gcpRepository, times(1)).findById(id);
        verify(gcpRepository, never()).delete(any(Gcp.class));
        verify(gcpRepository, never()).findAllByImageIdOrderByIndex(any(UUID.class));
        verify(gcpRepository, never()).saveAll(anyList());
    }
}