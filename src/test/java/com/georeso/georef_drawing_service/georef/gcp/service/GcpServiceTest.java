package com.georeso.georef_drawing_service.georef.gcp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.UUID;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.georeso.georef_drawing_service.georef.entity.Gcp;
import com.georeso.georef_drawing_service.georef.entity.GeorefImage;
import com.georeso.georef_drawing_service.georef.gcp.dto.GcpDto;
import com.georeso.georef_drawing_service.georef.gcp.exceptions.DuplicateGcpIndexException;
import com.georeso.georef_drawing_service.georef.gcp.exceptions.ImageNotFoundException;
import com.georeso.georef_drawing_service.georef.gcp.exceptions.InvalidGcpException;
import com.georeso.georef_drawing_service.georef.gcp.mapper.GcpMapper;
import com.georeso.georef_drawing_service.georef.gcp.repository.GcpRepository;
import com.georeso.georef_drawing_service.georef.image.repository.GeorefImageRepository;

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
    void shouldThrowWhenUnexpectedErrorOccurs() {
        // Given
        UUID imageId = UUID.randomUUID();
        GcpDto addGcpRequest = GcpDto.builder().imageId(imageId).build();
        GeorefImage image = new GeorefImage();

        when(imageRepository.findById(imageId)).thenReturn(Optional.of(image));
        when(gcpRepository.save(any())).thenThrow(new InvalidGcpException("Database down"));

        // When + Then
        assertThrows(InvalidGcpException.class, () -> gcpService.addGcp(addGcpRequest));
        verify(imageRepository, times(1)).findById(imageId);
        verify(gcpRepository, times(1)).save(any(Gcp.class));
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
}