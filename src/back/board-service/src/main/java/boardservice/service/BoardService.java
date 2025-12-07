package boardservice.service;

import boardservice.client.AuthServiceClient;
import boardservice.dto.*;
import boardservice.entity.Image;
import boardservice.entity.Pictogram;
import boardservice.events.StorageEventPublisher;
import boardservice.exception.UnauthorizedAccessException;
import boardservice.exception.ResourceNotFoundException;
import boardservice.exception.LanguageNotFoundException;
import boardservice.mapper.ImageMapper;
import boardservice.mapper.PictogramMapper;
import boardservice.repository.ImageRepository;
import boardservice.repository.LanguageRepository;
import boardservice.repository.PictogramRepository;
import boardservice.utils.ImageProcessor;
import boardservice.utils.ImageProcessor.ProcessedImage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final ImageRepository imageRepository;
    private final LanguageRepository languageRepository;
    private final PictogramRepository pictogramRepository;
    private final PictogramMapper pictogramMapper;
    private final ImageMapper imageMapper;
    private final AuthServiceClient authServiceClient;
    private final StorageEventPublisher storageEventPublisher;
    private final ImageProcessor imageProcessor;

    /**
     * Obtienes todos los pictogramas de un usuario dependiente
     * si este está vinculado al usuario que hace la petición
     */
    @Transactional
    public List<PictogramDto> getAllPictograms(String userId, GetAllPictogramsRequestDto request) {
        validateUserAccess(userId, request.ownerId());

        List<PictogramDto> response = new ArrayList<>();

        pictogramRepository.findAllByOwnerId(request.ownerId()).forEach(pictogram -> {
            response.add(pictogramMapper.toDto(pictogram));
        });

        return response;
    }

    /**
     * Sube una imagen para fabricar pictogramas, estarán asignados a un usuario y si este usuario
     * no tiene almacenamiento suficiente no se podrá subir.
     */
    @Transactional
    public ImageDto pushImage(String userId, ImagePushRequestDto request, MultipartFile file) {
        validateUserAccess(userId, request.ownerId());

        ProcessedImage processedImage = imageProcessor.process(file);
        authServiceClient.validateStorageLimit(UUID.fromString(userId), request.ownerId(), processedImage.sizeBytes());

        Image image = imageMapper.create(request, processedImage);
        Image imageSaved = imageRepository.save(image);

        storageEventPublisher.publishStorageDelta(request.ownerId(), processedImage.sizeBytes());

        return imageMapper.toDto(imageSaved);
    }

    /**
     * Elimina una imagen y sus pictogramas asociados (si los hay)
     */
    @Transactional
    public void deleteImage(String userId, UUID imageId, DeleteImageRequestDto requestDto) {
        validateUserAccess(userId, requestDto.ownerId());

        Image image = imageRepository.findByIdAndOwnerId(imageId, requestDto.ownerId())
                .orElseThrow(() -> new ResourceNotFoundException("Imagen no vinculada al usuario asignado o no encontrada"));

        List<Pictogram> pictograms = pictogramRepository.findAllByImageIdAndOwnerId(imageId, requestDto.ownerId());
        if (!pictograms.isEmpty()) {
            pictogramRepository.deleteAll(pictograms);
        }

        imageRepository.delete(image);
        storageEventPublisher.publishStorageDelta(requestDto.ownerId(), -image.getSizeBytes());
    }

    /**
     * Sube a la base de datos un pictograma tras varias verificaciones de propiedades
     */
    @Transactional
    public PictogramDto pushPictogram(String userId, PictogramPushRequestDto request) {
        validateUserAccess(userId, request.ownerId());

        Image image = imageRepository.findByIdAndOwnerId(request.imageId(), request.ownerId())
                .orElseThrow(() -> new ResourceNotFoundException("Imagen no vinculada al usuario asignado o no encontrada"));

        Pictogram pictogram = pictogramMapper.create(request, image);
        pictogramRepository.save(pictogram);
        return pictogramMapper.toDto(pictogram);
    }

    /**
     * Actualiza un pictograma existente
     */
    @Transactional
    public PictogramDto updatePictogram(String userId, UUID pictogramId, PictogramUpdateRequestDto request) {
        validateUserAccess(userId, request.ownerId());

        Pictogram pictogram = pictogramRepository.findByIdAndOwnerId(pictogramId, request.ownerId())
                .orElseThrow(() -> new ResourceNotFoundException("Pictograma no vinculado al usuario asignado"));

        if (request.name() != null) {
            pictogram.setName(request.name());
        }

        if (request.language() != null) {
            pictogram.setLanguage(languageRepository.findByCode(request.language())
                .orElseThrow(() -> new LanguageNotFoundException("Lenguaje no encontrado")));
        }

        Image image = null;
        if (request.imageId() != null) {
            image = imageRepository.findByIdAndOwnerId(request.imageId(), request.ownerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Imagen no vinculada al usuario asignado"));
        }

        if (image != null) {
            pictogram.setImage(image);
        }

        pictogramRepository.save(pictogram);
        return pictogramMapper.toDto(pictogram);
    }

    /**
     * Elimina un pictograma
     */
    @Transactional
    public void deletePictogram(String userId, UUID pictogramId, DeletePictogramRequestDto requestDto) {
        validateUserAccess(userId, requestDto.ownerId());

        Pictogram pictogram = pictogramRepository.findByIdAndOwnerId(pictogramId, requestDto.ownerId())
                .orElseThrow(() -> new ResourceNotFoundException("Pictograma no vinculado al usuario asignado"));

        pictogramRepository.delete(pictogram);
    }

    /**
     * Obtienes todas las imágenes de un usuario dependiente
     * si este está vinculado al usuario que hace la petición
     */
    @Transactional
    public List<ImageDto> getAllImages(String userId, GetAllPictogramsRequestDto request) {
        validateUserAccess(userId, request.ownerId());

        List<ImageDto> response = new ArrayList<>();

        imageRepository.findAllByOwnerId(request.ownerId()).forEach(image -> {
            response.add(imageMapper.toDto(image));
        });

        return response;
    }

    /**
     * Valida que el usuario que hace la petición tiene acceso al recurso del ownerId
     */
    private void validateUserAccess(String requestUserId, UUID ownerId) {
        UUID requestUserUuid = UUID.fromString(requestUserId);

        boolean hasAccess = authServiceClient.validateUserRelation(requestUserUuid, ownerId);

        if (!hasAccess) {
            throw new UnauthorizedAccessException("No tienes permiso para gestionar los recursos de este usuario");
        }
    }
}
