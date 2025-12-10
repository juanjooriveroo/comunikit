package boardservice.service;

import boardservice.dto.DeletePictogramRequestDto;
import boardservice.dto.GetAllPictogramsRequestDto;
import boardservice.dto.PictogramDto;
import boardservice.dto.PictogramPushRequestDto;
import boardservice.dto.PictogramUpdateRequestDto;
import boardservice.entity.Image;
import boardservice.entity.Language;
import boardservice.entity.Pictogram;
import boardservice.exception.LanguageNotFoundException;
import boardservice.exception.ResourceNotFoundException;
import boardservice.exception.UnauthorizedAccessException;
import boardservice.mapper.PictogramMapper;
import boardservice.repository.ImageRepository;
import boardservice.repository.LanguageRepository;
import boardservice.repository.PictogramRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PictogramServiceTest {

    @Mock
    private BoardService boardService;

    @Mock
    private LanguageRepository languageRepository;

    @Mock
    private PictogramRepository pictogramRepository;

    @Mock
    private PictogramMapper pictogramMapper;

    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private PictogramService pictogramService;

    private UUID requesterId;
    private UUID ownerId;
    private UUID pictogramId;
    private UUID imageId;
    private Language language;
    private Image image;
    private Pictogram pictogram;

    @BeforeEach
    void setUp() {
        requesterId = UUID.randomUUID();
        ownerId = UUID.randomUUID();
        pictogramId = UUID.randomUUID();
        imageId = UUID.randomUUID();

        language = new Language();
        language.setCode("es");
        language.setName("Español");

        image = Image.builder()
                .id(imageId)
                .ownerId(ownerId)
                .language(language)
                .build();

        pictogram = Pictogram.builder()
                .id(pictogramId)
                .name("casa")
                .ownerId(ownerId)
                .language(language)
                .image(image)
                .build();
    }

    @Test
    void getAllPictograms_ReturnsList_WhenAccessValid() {
        GetAllPictogramsRequestDto request = new GetAllPictogramsRequestDto(ownerId);
        PictogramDto dto = PictogramDto.builder().id(pictogramId).name("casa").build();

        doNothing().when(boardService).validateUserAccess(requesterId.toString(), ownerId);
        when(pictogramRepository.findAllByOwnerId(ownerId)).thenReturn(List.of(pictogram));
        when(pictogramMapper.toDto(pictogram)).thenReturn(dto);

        List<PictogramDto> result = pictogramService.getAllPictograms(requesterId.toString(), request);

        assertEquals(1, result.size());
            assertEquals(dto, result.get(0));
    }

    @Test
    void getAllPictograms_ThrowsWhenUnauthorized() {
        GetAllPictogramsRequestDto request = new GetAllPictogramsRequestDto(ownerId);
        doThrow(new UnauthorizedAccessException("No access")).when(boardService)
                .validateUserAccess(requesterId.toString(), ownerId);

        assertThrows(UnauthorizedAccessException.class, () ->
                pictogramService.getAllPictograms(requesterId.toString(), request)
        );
    }

    @Test
    void pushPictogram_Saves_WhenImageExists() {
        PictogramPushRequestDto request = new PictogramPushRequestDto(imageId, ownerId, "casa", "es");
        PictogramDto dto = PictogramDto.builder().id(pictogramId).name("casa").build();

        doNothing().when(boardService).validateUserAccess(requesterId.toString(), ownerId);
        when(imageRepository.findByIdAndOwnerId(imageId, ownerId)).thenReturn(Optional.of(image));
        when(pictogramMapper.create(request, image)).thenReturn(pictogram);
        when(pictogramMapper.toDto(pictogram)).thenReturn(dto);

        PictogramDto result = pictogramService.pushPictogram(requesterId.toString(), request);

        assertEquals(dto, result);
        verify(pictogramRepository).save(pictogram);
    }

    @Test
    void pushPictogram_ThrowsWhenImageMissing() {
        PictogramPushRequestDto request = new PictogramPushRequestDto(imageId, ownerId, "casa", "es");
        doNothing().when(boardService).validateUserAccess(requesterId.toString(), ownerId);
        when(imageRepository.findByIdAndOwnerId(imageId, ownerId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                pictogramService.pushPictogram(requesterId.toString(), request)
        );
    }

    @Test
    void updatePictogram_UpdatesFields_WhenFound() {
        PictogramUpdateRequestDto request = new PictogramUpdateRequestDto(ownerId, imageId, "hogar", "en");
        Language newLanguage = new Language();
        newLanguage.setCode("en");
        newLanguage.setName("English");

        PictogramDto dto = PictogramDto.builder().id(pictogramId).name("hogar").build();

        doNothing().when(boardService).validateUserAccess(requesterId.toString(), ownerId);
        when(pictogramRepository.findByIdAndOwnerId(pictogramId, ownerId)).thenReturn(Optional.of(pictogram));
        when(languageRepository.findByCode("en")).thenReturn(Optional.of(newLanguage));
        when(imageRepository.findByIdAndOwnerId(imageId, ownerId)).thenReturn(Optional.of(image));
        when(pictogramMapper.toDto(pictogram)).thenReturn(dto);

        PictogramDto result = pictogramService.updatePictogram(requesterId.toString(), pictogramId, request);

        assertEquals(dto, result);
        verify(pictogramRepository).save(pictogram);
        assertEquals("hogar", pictogram.getName());
        assertEquals(newLanguage, pictogram.getLanguage());
        assertEquals(image, pictogram.getImage());
    }

    @Test
    void updatePictogram_ThrowsWhenLanguageNotFound() {
        PictogramUpdateRequestDto request = new PictogramUpdateRequestDto(ownerId, imageId, "hogar", "en");
        doNothing().when(boardService).validateUserAccess(requesterId.toString(), ownerId);
        when(pictogramRepository.findByIdAndOwnerId(pictogramId, ownerId)).thenReturn(Optional.of(pictogram));
        when(languageRepository.findByCode("en")).thenReturn(Optional.empty());

        assertThrows(LanguageNotFoundException.class, () ->
                pictogramService.updatePictogram(requesterId.toString(), pictogramId, request)
        );
    }

    @Test
    void deletePictogram_Removes_WhenFound() {
        DeletePictogramRequestDto request = DeletePictogramRequestDto.builder().ownerId(ownerId).build();
        doNothing().when(boardService).validateUserAccess(requesterId.toString(), ownerId);
        when(pictogramRepository.findByIdAndOwnerId(pictogramId, ownerId)).thenReturn(Optional.of(pictogram));

        pictogramService.deletePictogram(requesterId.toString(), pictogramId, request);

        verify(pictogramRepository).delete(pictogram);
    }

    @Test
    void deletePictogram_ThrowsWhenMissing() {
        DeletePictogramRequestDto request = DeletePictogramRequestDto.builder().ownerId(ownerId).build();
        doNothing().when(boardService).validateUserAccess(requesterId.toString(), ownerId);
        when(pictogramRepository.findByIdAndOwnerId(pictogramId, ownerId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                pictogramService.deletePictogram(requesterId.toString(), pictogramId, request)
        );
    }
}
