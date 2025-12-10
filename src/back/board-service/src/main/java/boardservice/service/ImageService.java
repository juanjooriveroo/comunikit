package boardservice.service;

import boardservice.client.AuthServiceClient;
import boardservice.dto.DeleteImageRequestDto;
import boardservice.dto.GetAllPictogramsRequestDto;
import boardservice.dto.ImageDto;
import boardservice.dto.ImagePushRequestDto;
import boardservice.entity.Image;
import boardservice.entity.Pictogram;
import boardservice.events.StorageEventPublisher;
import boardservice.exception.ResourceNotFoundException;
import boardservice.mapper.ImageMapper;
import boardservice.repository.ImageRepository;
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
public class ImageService {

    private final BoardService boardService;
    private final AuthServiceClient authServiceClient;
    private final ImageRepository imageRepository;
    private final PictogramRepository pictogramRepository;
    private final ImageMapper imageMapper;
    private final StorageEventPublisher storageEventPublisher;
    private final ImageProcessor imageProcessor;

    @Transactional
    public ImageDto pushImage(String userId, ImagePushRequestDto request, MultipartFile file) {
        boardService.validateUserAccess(userId, request.ownerId());

        ProcessedImage processedImage = imageProcessor.process(file);
        authServiceClient.validateStorageLimit(UUID.fromString(userId), request.ownerId(), processedImage.sizeBytes());
        Image image = imageMapper.create(request, processedImage);
        Image imageSaved = imageRepository.save(image);

        storageEventPublisher.publishStorageDelta(request.ownerId(), processedImage.sizeBytes());

        return imageMapper.toDto(imageSaved);
    }

    @Transactional
    public void deleteImage(String userId, UUID imageId, DeleteImageRequestDto requestDto) {
        boardService.validateUserAccess(userId, requestDto.ownerId());

        Image image = imageRepository.findByIdAndOwnerId(imageId, requestDto.ownerId())
                .orElseThrow(() -> new ResourceNotFoundException("Imagen no vinculada al usuario asignado o no encontrada"));

        List<Pictogram> pictograms = pictogramRepository.findAllByImageIdAndOwnerId(imageId, requestDto.ownerId());
        if (!pictograms.isEmpty()) {
            pictogramRepository.deleteAll(pictograms);
        }

        imageRepository.delete(image);
        storageEventPublisher.publishStorageDelta(requestDto.ownerId(), -image.getSizeBytes());
    }

    @Transactional
    public List<ImageDto> getAllImages(String userId, GetAllPictogramsRequestDto request) {
        boardService.validateUserAccess(userId, request.ownerId());

        List<ImageDto> response = new ArrayList<>();
        imageRepository.findAllByOwnerId(request.ownerId()).forEach(image -> response.add(imageMapper.toDto(image)));
        return response;
    }
}
