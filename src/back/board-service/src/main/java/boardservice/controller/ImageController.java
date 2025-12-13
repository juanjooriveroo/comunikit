package boardservice.controller;

import boardservice.dto.DeleteImageRequestDto;
import boardservice.dto.GetAllPictogramsRequestDto;
import boardservice.dto.ImageDto;
import boardservice.dto.ImagePushRequestDto;
import boardservice.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Tag(
        name = "Imágenes",
        description = "Endpoints para la gestión de imágenes"
)
public class ImageController {
    private final ImageService imageService;

    @Operation(
            summary = "Subir imagen",
            description = "Sube una nueva imagen para asociarla a un pictograma",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Imagen subida exitosamente"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Petición inválida, archivo inválido o parámetros incorrectos"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Usuario no encontrado"
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Límite de almacenamiento excedido"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor"
                    )
            }
    )
    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> pushImage(
            @RequestHeader("X-User-ID") String userId,
            @RequestPart("data") ImagePushRequestDto request,
            @RequestPart("file") MultipartFile file
    ) {
        ImageDto response = imageService.pushImage(userId, request, file);
        return ResponseEntity.created(URI.create("/board/image/" + response.id())).body(response);
    }

    @Operation(
            summary = "Eliminar imagen",
            description = "Elimina una imagen existente y libera su espacio de almacenamiento",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Imagen eliminada exitosamente"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Petición inválida o parámetros incorrectos"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Imagen o usuario no encontrado"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor"
                    )
            }
    )
    @DeleteMapping("/image/{id}")
    public ResponseEntity<?> deleteImage(
            @RequestHeader("X-User-ID") String userId,
            @PathVariable UUID id,
            @RequestBody DeleteImageRequestDto request
    ) {
        imageService.deleteImage(userId, id, request);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Obtener todas las imágenes",
            description = "Obtiene la lista de todas las imágenes del usuario dependiente especificado",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Obtención exitosa de imágenes"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Petición inválida o parámetros incorrectos"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Usuario no encontrado"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor"
                    )
            }
    )
    @GetMapping("/getAll-images")
    public ResponseEntity<?> getAllImages(
            @RequestHeader("X-User-ID") String userId,
            @RequestParam("ownerId") UUID ownerId
    ) {
        GetAllPictogramsRequestDto request = new GetAllPictogramsRequestDto(ownerId);
        List<ImageDto> response = imageService.getAllImages(userId, request);
        return ResponseEntity.ok(response);
    }
}
