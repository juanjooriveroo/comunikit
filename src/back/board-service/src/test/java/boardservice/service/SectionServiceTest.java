package boardservice.service;

import boardservice.dto.DeleteSectionRequestDto;
import boardservice.dto.PictogramPositionRequestDto;
import boardservice.dto.SectionDto;
import boardservice.dto.SectionPushRequestDto;
import boardservice.dto.SectionUpdateRequestDto;
import boardservice.entity.Image;
import boardservice.entity.Language;
import boardservice.entity.Pictogram;
import boardservice.entity.Section;
import boardservice.entity.SectionPictogram;
import boardservice.exception.LanguageNotFoundException;
import boardservice.exception.ResourceNotFoundException;
import boardservice.exception.UnauthorizedAccessException;
import boardservice.mapper.SectionMapper;
import boardservice.repository.ImageRepository;
import boardservice.repository.LanguageRepository;
import boardservice.repository.PictogramRepository;
import boardservice.repository.SectionRepository;
import boardservice.utils.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SectionServiceTest {

    @Mock
    private UserValidator userValidator;

    @Mock
    private SectionRepository sectionRepository;

    @Mock
    private SectionMapper sectionMapper;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private LanguageRepository languageRepository;

    @Mock
    private PictogramRepository pictogramRepository;

    @InjectMocks
    private SectionService sectionService;

    private UUID requesterId;
    private UUID ownerId;
    private UUID sectionId;
    private UUID imageId;
    private UUID pictogramId;
    private Language language;
    private Image image;
    private Pictogram pictogram;
    private Section section;

    @BeforeEach
    void setUp() {
        requesterId = UUID.randomUUID();
        ownerId = UUID.randomUUID();
        sectionId = UUID.randomUUID();
        imageId = UUID.randomUUID();
        pictogramId = UUID.randomUUID();

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
                .ownerId(ownerId)
                .language(language)
                .image(image)
                .name("p1")
                .build();

        SectionPictogram sectionPictogram = SectionPictogram.builder()
                .sectionId(sectionId)
                .col(0)
                .row(0)
                .pictogram(pictogram)
                .build();

        section = Section.builder()
                .id(sectionId)
                .name("Casa")
                .language(language)
                .image(image)
                .ownerId(ownerId)
                .isPublic(false)
                .pictogramPositions(new ArrayList<>(List.of(sectionPictogram)))
                .build();
    }

    @Test
    void pushSection_Saves_WhenImageExists() {
        SectionPushRequestDto request = new SectionPushRequestDto(imageId, ownerId, "Hogar", "es");
        SectionDto dto = SectionDto.builder().id(sectionId).name("Hogar").build();

        doNothing().when(userValidator).validateUserAccess(requesterId.toString(), ownerId);
        when(imageRepository.findByIdAndOwnerId(imageId, ownerId)).thenReturn(Optional.of(image));
        when(sectionMapper.create(request, image)).thenReturn(section);
        when(sectionMapper.toDto(section)).thenReturn(dto);
        when(sectionRepository.save(section)).thenReturn(section);

        SectionDto result = sectionService.pushSection(requesterId.toString(), request);

        assertEquals(dto, result);
        verify(sectionRepository).save(section);
    }

    @Test
    void pushSection_ThrowsWhenImageMissing() {
        SectionPushRequestDto request = new SectionPushRequestDto(imageId, ownerId, "Hogar", "es");

        doNothing().when(userValidator).validateUserAccess(requesterId.toString(), ownerId);
        when(imageRepository.findByIdAndOwnerId(imageId, ownerId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> sectionService.pushSection(requesterId.toString(), request));
    }

    @Test
    void getAllSection_ReturnsList_WhenAccessValid() {
        SectionDto dto = SectionDto.builder().id(sectionId).name("Casa").build();

        doNothing().when(userValidator).validateUserAccess(requesterId.toString(), ownerId);
        when(sectionRepository.findAllByOwnerId(ownerId)).thenReturn(List.of(section));
        when(sectionMapper.toDto(section)).thenReturn(dto);

        List<SectionDto> result = sectionService.getAllSection(requesterId.toString(), ownerId);

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    void getAllSection_ThrowsWhenUnauthorized() {
        doThrow(new UnauthorizedAccessException("No access"))
                .when(userValidator).validateUserAccess(requesterId.toString(), ownerId);

        assertThrows(UnauthorizedAccessException.class,
                () -> sectionService.getAllSection(requesterId.toString(), ownerId));
    }

    @Test
    void deleteSection_Removes_WhenFound() {
        DeleteSectionRequestDto request = DeleteSectionRequestDto.builder().ownerId(ownerId).build();

        doNothing().when(userValidator).validateUserAccess(requesterId.toString(), ownerId);
        when(sectionRepository.findByIdAndOwnerId(sectionId, ownerId)).thenReturn(Optional.of(section));

        sectionService.deleteSection(requesterId.toString(), sectionId, request);

        verify(sectionRepository).delete(section);
    }

    @Test
    void deleteSection_ThrowsWhenNotFound() {
        DeleteSectionRequestDto request = DeleteSectionRequestDto.builder().ownerId(ownerId).build();

        doNothing().when(userValidator).validateUserAccess(requesterId.toString(), ownerId);
        when(sectionRepository.findByIdAndOwnerId(sectionId, ownerId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> sectionService.deleteSection(requesterId.toString(), sectionId, request));
    }

    @Test
    void updateSection_UpdatesFieldsAndOrder_WhenValid() {
        PictogramPositionRequestDto positionRequest = new PictogramPositionRequestDto(pictogramId, 2, 3);
        SectionUpdateRequestDto request = new SectionUpdateRequestDto(
                ownerId,
                imageId,
                "Nuevo nombre",
                "en",
                List.of(positionRequest)
        );

        Language newLanguage = new Language();
        newLanguage.setCode("en");
        newLanguage.setName("English");

        SectionDto dto = SectionDto.builder().id(sectionId).name("Nuevo nombre").build();

        section.setPictogramPositions(new ArrayList<>());

        doNothing().when(userValidator).validateUserAccess(requesterId.toString(), ownerId);
        when(sectionRepository.findByIdAndOwnerId(sectionId, ownerId)).thenReturn(Optional.of(section));
        when(languageRepository.findByCode("en")).thenReturn(Optional.of(newLanguage));
        when(imageRepository.findByIdAndOwnerId(imageId, ownerId)).thenReturn(Optional.of(image));
        when(pictogramRepository.findById(pictogramId)).thenReturn(Optional.of(pictogram));
        when(sectionMapper.toDto(section)).thenReturn(dto);

        SectionDto result = sectionService.updateSection(requesterId.toString(), sectionId, request);

        assertEquals(dto, result);
        assertEquals("Nuevo nombre", section.getName());
        assertEquals(newLanguage, section.getLanguage());
        assertEquals(image, section.getImage());
        assertEquals(1, section.getPictogramPositions().size());
        assertEquals(2, section.getPictogramPositions().get(0).getCol());
        assertEquals(3, section.getPictogramPositions().get(0).getRow());
        verify(sectionRepository).save(section);
    }

    @Test
    void updateSection_ThrowsWhenLanguageMissing() {
        PictogramPositionRequestDto positionRequest = new PictogramPositionRequestDto(pictogramId, 0, 0);
        SectionUpdateRequestDto request = new SectionUpdateRequestDto(
                ownerId,
                imageId,
                "Nuevo nombre",
                "en",
                List.of(positionRequest)
        );

        doNothing().when(userValidator).validateUserAccess(requesterId.toString(), ownerId);
        when(sectionRepository.findByIdAndOwnerId(sectionId, ownerId)).thenReturn(Optional.of(section));
        when(languageRepository.findByCode("en")).thenReturn(Optional.empty());

        assertThrows(LanguageNotFoundException.class,
                () -> sectionService.updateSection(requesterId.toString(), sectionId, request));
    }

    @Test
    void updateSection_ThrowsWhenPictogramMissing() {
        PictogramPositionRequestDto positionRequest = new PictogramPositionRequestDto(pictogramId, 0, 0);
        SectionUpdateRequestDto request = new SectionUpdateRequestDto(
                ownerId,
                imageId,
                "Nuevo nombre",
                "en",
                List.of(positionRequest)
        );

        doNothing().when(userValidator).validateUserAccess(requesterId.toString(), ownerId);
        when(sectionRepository.findByIdAndOwnerId(sectionId, ownerId)).thenReturn(Optional.of(section));
        when(languageRepository.findByCode("en")).thenReturn(Optional.of(language));
        when(imageRepository.findByIdAndOwnerId(imageId, ownerId)).thenReturn(Optional.of(image));
        section.setPictogramPositions(new ArrayList<>());
        when(pictogramRepository.findById(pictogramId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> sectionService.updateSection(requesterId.toString(), sectionId, request));
    }
}
