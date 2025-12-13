package boardservice.mapper;

import boardservice.dto.SectionDto;
import boardservice.dto.SectionPushRequestDto;
import boardservice.entity.Image;
import boardservice.entity.Section;
import boardservice.exception.LanguageNotFoundException;
import boardservice.repository.LanguageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class SectionMapper {

    private final ImageMapper imageMapper;
    private final PictogramMapper pictogramMapper;
    private final LanguageRepository languageRepository;

    public SectionDto toDto(Section section) {
        return SectionDto.builder()
                .id(section.getId())
                .name(section.getName())
                .language(section.getLanguage())
                .image(imageMapper.toDto(section.getImage()))
                .isPublic(section.isPublic())
                .pictograms(section.getPictograms().stream()
                        .map(pictogramMapper::toDto)
                        .toList())
                .build();
    }

    public Section create(SectionPushRequestDto request, Image image) {
        return Section.builder()
                .ownerId(request.ownerId())
                .name(request.name())
                .language(languageRepository.findByCode(request.language())
                        .orElseThrow(() -> new LanguageNotFoundException("Lenguaje no encontrado")))
                .isPublic(false)
                .pictograms(new ArrayList<>())
                .image(image)
                .build();
    }
}
