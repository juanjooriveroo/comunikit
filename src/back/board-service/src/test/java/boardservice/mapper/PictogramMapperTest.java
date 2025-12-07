package boardservice.mapper;

import boardservice.dto.PictogramDto;
import boardservice.dto.PictogramPushRequestDto;
import boardservice.entity.Image;
import boardservice.entity.Language;
import boardservice.entity.Pictogram;
import boardservice.repository.LanguageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PictogramMapperTest {

    @Mock
    private ImageMapper imageMapper;

    @Mock
    private LanguageRepository languageRepository;

    @InjectMocks
    private PictogramMapper pictogramMapper;

    private Language testLanguage;
    private Image testImage;
    private UUID ownerId;

    @BeforeEach
    void setUp() {
        ownerId = UUID.randomUUID();

        testLanguage = new Language();
        testLanguage.setCode("es");
        testLanguage.setName("Español");

        testImage = Image.builder()
                .id(UUID.randomUUID())
                .name("test.png")
                .ownerId(ownerId)
                .build();
    }

    @Test
    void toDto_ShouldConvertPictogramToDto() {
        Pictogram pictogram = Pictogram.builder()
                .id(UUID.randomUUID())
                .name("casa")
                .language(testLanguage)
                .image(testImage)
                .build();

        PictogramDto result = pictogramMapper.toDto(pictogram);

        assertNotNull(result);
        assertEquals(pictogram.getId(), result.id());
        assertEquals(pictogram.getName(), result.name());
        assertEquals(pictogram.getLanguage(), result.language());
        verify(imageMapper).toDto(testImage);
    }

    @Test
    void create_ShouldCreatePictogramFromRequest() {
        PictogramPushRequestDto request = new PictogramPushRequestDto(
                testImage.getId(),
                ownerId,
                "casa",
                "es"
        );

        when(languageRepository.findByCode("es")).thenReturn(Optional.of(testLanguage));

        Pictogram result = pictogramMapper.create(request, testImage);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("casa", result.getName());
        assertEquals(ownerId, result.getOwnerId());
        assertEquals(testImage, result.getImage());
        assertEquals(testLanguage, result.getLanguage());
        verify(languageRepository).findByCode("es");
    }

    @Test
    void create_ShouldThrowException_WhenLanguageNotFound() {
        PictogramPushRequestDto request = new PictogramPushRequestDto(
                testImage.getId(),
                ownerId,
                "casa",
                "xx"
        );

        when(languageRepository.findByCode("xx")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> pictogramMapper.create(request, testImage));
        verify(languageRepository).findByCode("xx");
    }
}
