package boardservice.service;

import boardservice.dto.DeletePictogramRequestDto;
import boardservice.dto.GetAllPictogramsRequestDto;
import boardservice.dto.PictogramDto;
import boardservice.dto.PictogramPushRequestDto;
import boardservice.dto.PictogramUpdateRequestDto;
import boardservice.entity.Image;
import boardservice.entity.Pictogram;
import boardservice.exception.LanguageNotFoundException;
import boardservice.exception.ResourceNotFoundException;
import boardservice.mapper.PictogramMapper;
import boardservice.repository.ImageRepository;
import boardservice.repository.LanguageRepository;
import boardservice.repository.PictogramRepository;
import boardservice.utils.UserValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PictogramService {

    private final UserValidator userValidator;
    private final LanguageRepository languageRepository;
    private final PictogramRepository pictogramRepository;
    private final PictogramMapper pictogramMapper;
    private final ImageRepository imageRepository;

    @Transactional
    public List<PictogramDto> getAllPictograms(String userId, GetAllPictogramsRequestDto request) {
        userValidator.validateUserAccess(userId, request.ownerId());

        List<PictogramDto> response = new ArrayList<>();
        pictogramRepository.findAllByOwnerId(request.ownerId()).forEach(pictogram -> response.add(pictogramMapper.toDto(pictogram)));
        return response;
    }

    @Transactional
    public PictogramDto pushPictogram(String userId, PictogramPushRequestDto request) {
        userValidator.validateUserAccess(userId, request.ownerId());

        Image image = imageRepository.findByIdAndOwnerId(request.imageId(), request.ownerId())
                .orElseThrow(() -> new ResourceNotFoundException("Imagen no vinculada al usuario asignado o no encontrada"));

        Pictogram pictogram = pictogramMapper.create(request, image);
        pictogramRepository.save(pictogram);
        return pictogramMapper.toDto(pictogram);
    }

    @Transactional
    public PictogramDto updatePictogram(String userId, UUID pictogramId, PictogramUpdateRequestDto request) {
        userValidator.validateUserAccess(userId, request.ownerId());

        Pictogram pictogram = pictogramRepository.findByIdAndOwnerId(pictogramId, request.ownerId())
                .orElseThrow(() -> new ResourceNotFoundException("Pictograma no vinculado al usuario asignado"));

        if (request.name() != null) {
            pictogram.setName(request.name());
        }

        if (request.language() != null) {
            pictogram.setLanguage(languageRepository.findByCode(request.language())
                    .orElseThrow(() -> new LanguageNotFoundException("Lenguaje no encontrado")));
        }

        if (request.imageId() != null) {
            Image image = imageRepository.findByIdAndOwnerId(request.imageId(), request.ownerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Imagen no vinculada al usuario asignado"));
            pictogram.setImage(image);
        }

        pictogramRepository.save(pictogram);
        return pictogramMapper.toDto(pictogram);
    }

    @Transactional
    public void deletePictogram(String userId, UUID pictogramId, DeletePictogramRequestDto requestDto) {
        userValidator.validateUserAccess(userId, requestDto.ownerId());

        Pictogram pictogram = pictogramRepository.findByIdAndOwnerId(pictogramId, requestDto.ownerId())
                .orElseThrow(() -> new ResourceNotFoundException("Pictograma no vinculado al usuario asignado"));

        pictogramRepository.delete(pictogram);
    }
}
