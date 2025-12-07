package boardservice.mapper;

import boardservice.dto.ImageDto;
import boardservice.dto.ImagePushRequestDto;
import boardservice.entity.Image;
import boardservice.exception.LanguageNotFoundException;
import boardservice.repository.LanguageRepository;
import boardservice.utils.ImageProcessor.ProcessedImage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImageMapper {

    private final LanguageRepository languageRepository;

    public ImageDto toDto(Image image) {
        return ImageDto.builder()
                .id(image.getId())
                .language(image.getLanguage())
                .image(image.getImage())
                .name(image.getName())
                .mimeType(image.getMimeType())
                .sizeBytes(image.getSizeBytes())
                .isPublic(image.isPublic())
                .build();
    }

    public Image create(ImagePushRequestDto request, ProcessedImage processedImage) {
        return Image.builder()
                .name(request.name())
                .ownerId(request.ownerId())
                .isPublic(false)
                .language(languageRepository.findByCode(request.language())
                        .orElseThrow(() -> new LanguageNotFoundException("Lenguaje no encontrado")))
                .image(processedImage.bytes())
                .mimeType(processedImage.mimeType())
                .sizeBytes(processedImage.sizeBytes())
                .build();
    }
}
