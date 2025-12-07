package boardservice.mapper;

import boardservice.dto.ImageDto;
import boardservice.dto.ImagePushRequestDto;
import boardservice.entity.Image;
import boardservice.entity.Language;
import boardservice.repository.LanguageRepository;
import boardservice.utils.ImageProcessor.ProcessedImage;

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
class ImageMapperTest {

    @Mock
    private LanguageRepository languageRepository;

    @InjectMocks
    private ImageMapper imageMapper;

    private Language testLanguage;
    private UUID ownerId;

    @BeforeEach
    void setUp() {
        testLanguage = new Language();
        testLanguage.setCode("es");
        testLanguage.setName("Español");
        ownerId = UUID.randomUUID();
    }

    @Test
    void toDto_ShouldConvertImageToDto() {
        Image image = Image.builder()
                .id(UUID.randomUUID())
                .name("test.png")
                .image(new byte[]{1, 2, 3})
                .language(testLanguage)
                .mimeType("image/png")
                .sizeBytes(1024L)
                .isPublic(false)
                .build();

        ImageDto result = imageMapper.toDto(image);

        assertNotNull(result);
        assertEquals(image.getId(), result.id());
        assertEquals(image.getName(), result.name());
        assertEquals(image.getLanguage(), result.language());
        assertEquals(image.getMimeType(), result.mimeType());
        assertEquals(image.getSizeBytes(), result.sizeBytes());
        assertFalse(result.isPublic());
    }

    @Test
    void create_ShouldCreateImageFromRequestAndProcessedImage() {
        ImagePushRequestDto request = new ImagePushRequestDto("test.png", "es", ownerId);
        ProcessedImage processedImage = new ProcessedImage(new byte[]{1, 2, 3}, "image/png", 1024L);

        when(languageRepository.findByCode("es")).thenReturn(Optional.of(testLanguage));

        Image result = imageMapper.create(request, processedImage);

        assertNotNull(result);
        assertEquals("test.png", result.getName());
        assertEquals(ownerId, result.getOwnerId());
        assertEquals(testLanguage, result.getLanguage());
        assertEquals("image/png", result.getMimeType());
        assertEquals(1024L, result.getSizeBytes());
        assertFalse(result.isPublic());
        verify(languageRepository).findByCode("es");
    }

    @Test
    void create_ShouldThrowException_WhenLanguageNotFound() {
        ImagePushRequestDto request = new ImagePushRequestDto("test.png", "xx", ownerId);
        ProcessedImage processedImage = new ProcessedImage(new byte[]{1, 2, 3}, "image/png", 1024L);

        when(languageRepository.findByCode("xx")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> imageMapper.create(request, processedImage));
        verify(languageRepository).findByCode("xx");
    }
}
