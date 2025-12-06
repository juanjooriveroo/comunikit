package boardservice.service;

import boardservice.client.AuthServiceClient;
import boardservice.dto.*;
import boardservice.entity.Image;
import boardservice.entity.Pictogram;
import boardservice.exception.UnauthorizedAccessException;
import boardservice.mapper.ImageMapper;
import boardservice.mapper.PictogramMapper;
import boardservice.repository.ImageRepository;
import boardservice.repository.PictogramRepository;
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
    private final PictogramRepository pictogramRepository;
    private final PictogramMapper pictogramMapper;
    private final ImageMapper imageMapper;
    private final AuthServiceClient authServiceClient;

    @Transactional
    public List<PictogramDto> getAllPictograms(String userId, GetAllPictogramsRequestDto request) {
        validateUserAccess(userId, request.ownerId());

        List<PictogramDto> response = new ArrayList<>();

        pictogramRepository.findAllByOwnerId(request.ownerId()).forEach(pictogram -> {
            response.add(pictogramMapper.toDto(pictogram));
        });

        return response;
    }

    @Transactional
    public ImageDto pushImage(String userId, ImagePushRequestDto request, MultipartFile file) {
        validateUserAccess(userId, request.ownerId());
        
        Image image = imageMapper.create(request, file);
        Image imageSaved = imageRepository.save(image);
        return imageMapper.toDto(imageSaved);
    }

    @Transactional
    public PictogramDto pushPictogram(String userId, PictogramPushRequestDto request) {
        validateUserAccess(userId, request.ownerId());
        
        Pictogram pictogram = pictogramMapper.create(request);
        pictogramRepository.save(pictogram);
        return pictogramMapper.toDto(pictogram);
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
