package boardservice.mapper;

import boardservice.dto.PictogramDto;
import boardservice.dto.PictogramPushRequestDto;
import boardservice.entity.Pictogram;
import boardservice.entity.Image;
import boardservice.exception.LanguageNotFoundException;
import boardservice.repository.LanguageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PictogramMapper {

        private final ImageMapper imageMapper;
        private final LanguageRepository languageRepository;

    public PictogramDto toDto(Pictogram pictogram){
        return PictogramDto.builder()
                .id(pictogram.getId())
                .name(pictogram.getName())
                .language(pictogram.getLanguage())
                .image(imageMapper.toDto(pictogram.getImage()))
                .is_public(pictogram.isPublic())
                .build();
    }

        public Pictogram create(PictogramPushRequestDto request, Image image) {
        return Pictogram.builder()
                .id(UUID.randomUUID())
                .ownerId(request.ownerId())
                                .image(image)
                .name(request.name())
                .language(languageRepository.findByCode(request.language())
                        .orElseThrow(() -> new LanguageNotFoundException("Lenguaje no encontrado")))
                .isPublic(false)
                .build();
    }
}
