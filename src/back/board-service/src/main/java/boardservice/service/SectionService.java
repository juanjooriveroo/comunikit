package boardservice.service;

import boardservice.dto.*;
import boardservice.entity.Image;
import boardservice.entity.Pictogram;
import boardservice.entity.Section;
import boardservice.entity.SectionPictogram;
import boardservice.exception.LanguageNotFoundException;
import boardservice.exception.ResourceNotFoundException;
import boardservice.mapper.SectionMapper;
import boardservice.repository.ImageRepository;
import boardservice.repository.LanguageRepository;
import boardservice.repository.PictogramRepository;
import boardservice.repository.SectionRepository;
import boardservice.utils.UserValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SectionService {
    private final UserValidator userValidator;
    private final SectionRepository sectionRepository;
    private final SectionMapper sectionMapper;
    private final ImageRepository imageRepository;
    private final LanguageRepository languageRepository;
    private final PictogramRepository pictogramRepository;

    @Transactional
    public SectionDto pushSection(String userId, SectionPushRequestDto request) {
        userValidator.validateUserAccess(userId, request.ownerId());

        Image image = imageRepository.findByIdAndOwnerId(request.imageId(), request.ownerId())
                .orElseThrow(() -> new ResourceNotFoundException("Imagen no vinculada al usuario asignado o no encontrada"));

        Section section = sectionMapper.create(request, image);
        section = sectionRepository.save(section);
        return sectionMapper.toDto(section);
    }

    @Transactional
    public void deleteSection(String userId, UUID id, DeleteSectionRequestDto request) {
        userValidator.validateUserAccess(userId, request.ownerId());

        Section section = sectionRepository.findByIdAndOwnerId(id, request.ownerId())
                .orElseThrow(() -> new ResourceNotFoundException("Sección no vinculado al usuario asignado"));

        sectionRepository.delete(section);
    }

    @Transactional
    public List<SectionDto> getAllSection(String userId, UUID ownerId) {
        userValidator.validateUserAccess(userId, ownerId);

        List<SectionDto> response = new ArrayList<>();
        sectionRepository.findAllByOwnerId(ownerId).forEach(section -> response.add(sectionMapper.toDto(section)));
        return response;
    }

    @Transactional
    public SectionDto updateSection(String userId, UUID id, SectionUpdateRequestDto request){
        userValidator.validateUserAccess(userId, request.ownerId());

        Section section = sectionRepository.findByIdAndOwnerId(id, request.ownerId())
                .orElseThrow(() -> new ResourceNotFoundException("Sección no vinculado al usuario asignado"));

        if (request.name() != null) {
            section.setName(request.name());
        }

        if (request.language() != null) {
            section.setLanguage(languageRepository.findByCode(request.language())
                    .orElseThrow(() -> new LanguageNotFoundException("Lenguaje no encontrado")));
        }

        if (request.imageId() != null) {
            Image image = imageRepository.findByIdAndOwnerId(request.imageId(), request.ownerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Imagen no vinculada al usuario asignado"));
            section.setImage(image);
        }

        if (request.pictograms() != null) {
            // Limpiar posiciones actuales
            section.getPictogramPositions().clear();

            // Añadir nuevas posiciones
            for (PictogramPositionRequestDto pos : request.pictograms()) {
                // Validar límites del grid 5x6
                if (pos.col() < 0 || pos.col() > 4 || pos.row() < 0 || pos.row() > 5) {
                    throw new IllegalArgumentException("Posición fuera del grid 5x6: col=" + pos.col() + ", row=" + pos.row());
                }

                Pictogram pictogram = pictogramRepository.findById(pos.pictogramId())
                        .orElseThrow(() -> new ResourceNotFoundException("Pictograma no encontrado: " + pos.pictogramId()));

                SectionPictogram sp = SectionPictogram.builder()
                        .sectionId(section.getId())
                        .col(pos.col())
                        .row(pos.row())
                        .pictogram(pictogram)
                        .build();

                section.getPictogramPositions().add(sp);
            }
        }

        sectionRepository.save(section);
        return sectionMapper.toDto(section);
    }
}
