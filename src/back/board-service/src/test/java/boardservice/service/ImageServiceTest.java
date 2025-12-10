package boardservice.service;

import boardservice.client.AuthServiceClient;
import boardservice.dto.DeleteImageRequestDto;
import boardservice.dto.GetAllPictogramsRequestDto;
import boardservice.dto.ImageDto;
import boardservice.dto.ImagePushRequestDto;
import boardservice.entity.Image;
import boardservice.entity.Pictogram;
import boardservice.events.StorageEventPublisher;
import boardservice.exception.ResourceNotFoundException;
import boardservice.mapper.ImageMapper;
import boardservice.repository.ImageRepository;
import boardservice.repository.PictogramRepository;
import boardservice.utils.ImageProcessor;
import boardservice.utils.ImageProcessor.ProcessedImage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @Mock
    private BoardService boardService;

    @Mock
    private AuthServiceClient authServiceClient;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private PictogramRepository pictogramRepository;

    @Mock
    private ImageMapper imageMapper;

    @Mock
    private StorageEventPublisher storageEventPublisher;

    @Mock
    private ImageProcessor imageProcessor;

    @InjectMocks
    private ImageService imageService;

    private UUID requesterId;
    private UUID ownerId;
    private UUID imageId;
    private Image image;
    private MultipartFile file;
    private ProcessedImage processedImage;

    @BeforeEach
    void setUp() {
        requesterId = UUID.randomUUID();
        ownerId = UUID.randomUUID();
        imageId = UUID.randomUUID();

        image = Image.builder()
                .id(imageId)
                .name("test.png")
                .ownerId(ownerId)
                .sizeBytes(1024L)
                .build();

        file = mock(MultipartFile.class);
        processedImage = new ProcessedImage(new byte[]{1, 2, 3}, "image/png", 1024L);
    }

    @Test
    void pushImage_Saves_WhenValid() {
        ImagePushRequestDto request = new ImagePushRequestDto("test.png", "es", ownerId);
        ImageDto dto = ImageDto.builder().id(imageId).name("test.png").build();

        doNothing().when(boardService).validateUserAccess(requesterId.toString(), ownerId);
        when(imageProcessor.process(file)).thenReturn(processedImage);
        when(authServiceClient.validateStorageLimit(requesterId, ownerId, processedImage.sizeBytes()))
                .thenReturn(null);
        when(imageMapper.create(request, processedImage)).thenReturn(image);
        when(imageRepository.save(image)).thenReturn(image);
        when(imageMapper.toDto(image)).thenReturn(dto);

        ImageDto result = imageService.pushImage(requesterId.toString(), request, file);

        assertEquals(dto, result);
        verify(storageEventPublisher).publishStorageDelta(ownerId, processedImage.sizeBytes());
    }

    @Test
    void pushImage_PropagatesStorageLimitError() {
        ImagePushRequestDto request = new ImagePushRequestDto("test.png", "es", ownerId);

        doNothing().when(boardService).validateUserAccess(requesterId.toString(), ownerId);
        when(imageProcessor.process(file)).thenReturn(processedImage);
        when(authServiceClient.validateStorageLimit(requesterId, ownerId, processedImage.sizeBytes()))
                .thenThrow(new RuntimeException("limit"));

        assertThrows(RuntimeException.class, () -> imageService.pushImage(requesterId.toString(), request, file));
        verify(imageRepository, never()).save(any());
        verify(storageEventPublisher, never()).publishStorageDelta(any(), anyLong());
    }

    @Test
    void deleteImage_RemovesImageAndPictograms() {
        DeleteImageRequestDto request = DeleteImageRequestDto.builder().ownerId(ownerId).build();
        Pictogram pictogram = Pictogram.builder().image(image).ownerId(ownerId).build();

        doNothing().when(boardService).validateUserAccess(requesterId.toString(), ownerId);
        when(imageRepository.findByIdAndOwnerId(imageId, ownerId)).thenReturn(Optional.of(image));
        when(pictogramRepository.findAllByImageIdAndOwnerId(imageId, ownerId)).thenReturn(List.of(pictogram));

        imageService.deleteImage(requesterId.toString(), imageId, request);

        verify(pictogramRepository).deleteAll(List.of(pictogram));
        verify(imageRepository).delete(image);
        verify(storageEventPublisher).publishStorageDelta(ownerId, -image.getSizeBytes());
    }

    @Test
    void deleteImage_ThrowsWhenNotFound() {
        DeleteImageRequestDto request = DeleteImageRequestDto.builder().ownerId(ownerId).build();

        doNothing().when(boardService).validateUserAccess(requesterId.toString(), ownerId);
        when(imageRepository.findByIdAndOwnerId(imageId, ownerId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                imageService.deleteImage(requesterId.toString(), imageId, request)
        );
    }

    @Test
    void getAllImages_ReturnsList() {
        GetAllPictogramsRequestDto request = new GetAllPictogramsRequestDto(ownerId);
        ImageDto dto = ImageDto.builder().id(imageId).name("test.png").build();

        doNothing().when(boardService).validateUserAccess(requesterId.toString(), ownerId);
        when(imageRepository.findAllByOwnerId(ownerId)).thenReturn(List.of(image));
        when(imageMapper.toDto(image)).thenReturn(dto);

        List<ImageDto> result = imageService.getAllImages(requesterId.toString(), request);

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }
}
