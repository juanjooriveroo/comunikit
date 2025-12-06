package boardservice.mapper;

import boardservice.dto.PictogramDto;
import boardservice.dto.PictogramPushRequestDto;
import boardservice.entity.Pictogram;
import boardservice.repository.ImageRepository;
import boardservice.repository.LanguageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PictogramMapper {

    private final ImageMapper imageMapper;
    private final ImageRepository imageRepository;
    private final LanguageRepository languageRepository;

    public PictogramDto toDto(Pictogram pictogram){
        return PictogramDto.builder()
                .id(pictogram.getId())
                .name(pictogram.getName())
                .language(pictogram.getLanguage())
                .image(imageMapper.toDto(pictogram.getImage()))
                .build();
    }

    public Pictogram create(PictogramPushRequestDto request) {
        return Pictogram.builder()
                .id(UUID.randomUUID())
                .ownerId(request.ownerId())
                .image(imageRepository.findById(request.imageId())
                        .orElseThrow(() -> new RuntimeException("Image no encontrada")))
                .name(request.name())
                .language(languageRepository.findByCode(request.language())
                        .orElseThrow(() -> new RuntimeException("Lenguaje no encontrado")))
                .build();
    }
}
