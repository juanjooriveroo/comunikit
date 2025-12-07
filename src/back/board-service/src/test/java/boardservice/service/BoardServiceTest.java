package boardservice.service;

import boardservice.client.AuthServiceClient;
import boardservice.dto.*;
import boardservice.entity.Image;
import boardservice.entity.Language;
import boardservice.entity.Pictogram;
import boardservice.events.StorageEventPublisher;
import boardservice.exception.UnauthorizedAccessException;
import boardservice.mapper.ImageMapper;
import boardservice.mapper.PictogramMapper;
import boardservice.repository.ImageRepository;
import boardservice.repository.LanguageRepository;
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
class BoardServiceTest {

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private PictogramRepository pictogramRepository;

    @Mock
    private PictogramMapper pictogramMapper;

    @Mock
    private ImageMapper imageMapper;

    @Mock
    private AuthServiceClient authServiceClient;

    @Mock
    private StorageEventPublisher storageEventPublisher;

    @Mock
    private ImageProcessor imageProcessor;

    @Mock
    private LanguageRepository languageRepository;

    @InjectMocks
    private BoardService boardService;

    private UUID userId;
    private UUID ownerId;
    private UUID imageId;
    private UUID pictogramId;
    private Image testImage;
    private Pictogram testPictogram;
    private Language testLanguage;
    private MultipartFile testFile;
    private ProcessedImage processedImage;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        ownerId = UUID.randomUUID();
        imageId = UUID.randomUUID();
        pictogramId = UUID.randomUUID();

        testLanguage = new Language();
        testLanguage.setCode("es");
        testLanguage.setName("Español");

        testImage = Image.builder()
                .id(imageId)
                .name("test.png")
                .ownerId(ownerId)
                .language(testLanguage)
                .image(new byte[]{1, 2, 3})
                .mimeType("image/png")
                .sizeBytes(1024L)
                .isPublic(false)
                .build();

        testPictogram = Pictogram.builder()
                .id(pictogramId)
                .name("casa")
                .ownerId(ownerId)
                .language(testLanguage)
                .image(testImage)
                .build();

        testFile = mock(MultipartFile.class);
        processedImage = new ProcessedImage(new byte[]{1, 2, 3}, "image/png", 1024L);
    }

    @Test
    void getAllPictograms_ShouldReturnPictograms_WhenUserHasAccess() {
        GetAllPictogramsRequestDto request = new GetAllPictogramsRequestDto(ownerId);
        PictogramDto expectedDto = PictogramDto.builder()
                .id(pictogramId)
                .name("casa")
                .language(testLanguage)
                .build();

        when(authServiceClient.validateUserRelation(userId, ownerId)).thenReturn(true);
        when(pictogramRepository.findAllByOwnerId(ownerId)).thenReturn(List.of(testPictogram));
        when(pictogramMapper.toDto(testPictogram)).thenReturn(expectedDto);

        List<PictogramDto> result = boardService.getAllPictograms(userId.toString(), request);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedDto, result.get(0));
        verify(authServiceClient).validateUserRelation(userId, ownerId);
        verify(pictogramRepository).findAllByOwnerId(ownerId);
    }

    @Test
    void getAllPictograms_ShouldThrowUnauthorizedAccessException_WhenUserHasNoAccess() {
        GetAllPictogramsRequestDto request = new GetAllPictogramsRequestDto(ownerId);

        when(authServiceClient.validateUserRelation(userId, ownerId)).thenReturn(false);

        assertThrows(UnauthorizedAccessException.class, () ->
                boardService.getAllPictograms(userId.toString(), request)
        );

        verify(authServiceClient).validateUserRelation(userId, ownerId);
        verifyNoInteractions(pictogramRepository);
    }

    @Test
    void pushImage_ShouldSaveImage_WhenStorageLimitIsNotExceeded() {
        ImagePushRequestDto request = new ImagePushRequestDto("test.png", "es", ownerId);
        ImageDto expectedDto = ImageDto.builder()
                .id(imageId)
                .name("test.png")
                .build();

        when(authServiceClient.validateUserRelation(userId, ownerId)).thenReturn(true);
        when(imageProcessor.process(testFile)).thenReturn(processedImage);
        when(authServiceClient.validateStorageLimit(userId, ownerId, processedImage.sizeBytes()))
                .thenReturn(new StorageValidationResponseDto(true, 100f, 1f, 99f));
        when(imageMapper.create(request, processedImage)).thenReturn(testImage);
        when(imageRepository.save(testImage)).thenReturn(testImage);
        when(imageMapper.toDto(testImage)).thenReturn(expectedDto);

        ImageDto result = boardService.pushImage(userId.toString(), request, testFile);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(imageProcessor).process(testFile);
        verify(authServiceClient).validateStorageLimit(userId, ownerId, processedImage.sizeBytes());
        verify(imageRepository).save(testImage);
        verify(storageEventPublisher).publishStorageDelta(ownerId, processedImage.sizeBytes());
    }

    @Test
    void pushImage_ShouldThrowException_WhenStorageLimitExceeded() {
        ImagePushRequestDto request = new ImagePushRequestDto("test.png", "es", ownerId);

        when(authServiceClient.validateUserRelation(userId, ownerId)).thenReturn(true);
        when(imageProcessor.process(testFile)).thenReturn(processedImage);
        when(authServiceClient.validateStorageLimit(userId, ownerId, processedImage.sizeBytes()))
                .thenThrow(new RuntimeException("La imagen supera el límite de almacenamiento"));

        assertThrows(RuntimeException.class, () ->
                boardService.pushImage(userId.toString(), request, testFile)
        );

        verify(imageProcessor).process(testFile);
        verify(imageRepository, never()).save(any());
        verify(storageEventPublisher, never()).publishStorageDelta(any(), anyLong());
    }

    @Test
    void deleteImage_ShouldDeleteImageAndPictograms_WhenImageExists() {
        when(authServiceClient.validateUserRelation(userId, ownerId)).thenReturn(true);
        when(imageRepository.findByIdAndOwnerId(imageId, ownerId)).thenReturn(Optional.of(testImage));
        when(pictogramRepository.findAllByImageIdAndOwnerId(imageId, ownerId))
                .thenReturn(List.of(testPictogram));

        boardService.deleteImage(userId.toString(), imageId, DeleteImageRequestDto.builder().ownerId(ownerId).build());

        verify(pictogramRepository).deleteAll(List.of(testPictogram));
        verify(imageRepository).delete(testImage);
        verify(storageEventPublisher).publishStorageDelta(ownerId, -1024L);
    }

    @Test
    void deleteImage_ShouldThrowException_WhenImageNotFound() {
        when(authServiceClient.validateUserRelation(userId, ownerId)).thenReturn(true);
        when(imageRepository.findByIdAndOwnerId(imageId, ownerId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                boardService.deleteImage(userId.toString(), imageId, DeleteImageRequestDto.builder().ownerId(ownerId).build())
        );

        verify(imageRepository, never()).delete(any());
        verify(storageEventPublisher, never()).publishStorageDelta(any(), anyLong());
    }

    @Test
    void pushPictogram_ShouldSavePictogram_WhenImageExists() {
        PictogramPushRequestDto request = new PictogramPushRequestDto(imageId, ownerId, "casa", "es");
        PictogramDto expectedDto = PictogramDto.builder()
                .id(pictogramId)
                .name("casa")
                .build();

        when(authServiceClient.validateUserRelation(userId, ownerId)).thenReturn(true);
        when(imageRepository.findByIdAndOwnerId(imageId, ownerId)).thenReturn(Optional.of(testImage));
        when(pictogramMapper.create(request, testImage)).thenReturn(testPictogram);
        when(pictogramRepository.save(testPictogram)).thenReturn(testPictogram);
        when(pictogramMapper.toDto(testPictogram)).thenReturn(expectedDto);

        PictogramDto result = boardService.pushPictogram(userId.toString(), request);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(pictogramRepository).save(testPictogram);
    }

    @Test
    void pushPictogram_ShouldThrowException_WhenImageNotOwnedByUser() {
        PictogramPushRequestDto request = new PictogramPushRequestDto(imageId, ownerId, "casa", "es");

        when(authServiceClient.validateUserRelation(userId, ownerId)).thenReturn(true);
        when(imageRepository.findByIdAndOwnerId(imageId, ownerId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                boardService.pushPictogram(userId.toString(), request)
        );

        verify(pictogramRepository, never()).save(any());
    }

    @Test
    void updatePictogram_ShouldUpdatePictogram_WhenPictogramExists() {
        PictogramUpdateRequestDto request = new PictogramUpdateRequestDto(ownerId, null, "hogar", "en");
        PictogramDto expectedDto = PictogramDto.builder().id(pictogramId).name("hogar").build();

        when(authServiceClient.validateUserRelation(userId, ownerId)).thenReturn(true);
        when(pictogramRepository.findByIdAndOwnerId(pictogramId, ownerId)).thenReturn(Optional.of(testPictogram));
        when(pictogramRepository.save(testPictogram)).thenReturn(testPictogram);
        when(pictogramMapper.toDto(testPictogram)).thenReturn(expectedDto);

        PictogramDto result = boardService.updatePictogram(userId.toString(), pictogramId, request);

        assertNotNull(result);
        verify(pictogramRepository).save(testPictogram);
    }

    @Test
    void deletePictogram_ShouldDeletePictogram_WhenPictogramExists() {
        when(authServiceClient.validateUserRelation(userId, ownerId)).thenReturn(true);
        when(pictogramRepository.findByIdAndOwnerId(pictogramId, ownerId)).thenReturn(Optional.of(testPictogram));

        boardService.deletePictogram(userId.toString(), pictogramId, DeletePictogramRequestDto.builder().ownerId(ownerId).build());

        verify(pictogramRepository).delete(testPictogram);
    }

    @Test
    void deletePictogram_ShouldThrowException_WhenPictogramNotFound() {
        when(authServiceClient.validateUserRelation(userId, ownerId)).thenReturn(true);
        when(pictogramRepository.findByIdAndOwnerId(pictogramId, ownerId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                boardService.deletePictogram(userId.toString(), pictogramId, DeletePictogramRequestDto.builder().ownerId(ownerId).build())
        );

        verify(pictogramRepository, never()).delete(any());
    }
}
