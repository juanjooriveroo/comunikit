package boardservice.controller;

import boardservice.dto.*;
import boardservice.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
@Tag(
        name = "Tablero",
        description = "Endpoints para la gestión y uso de tableros y pictográmas"
)
public class BoardController {

    private final BoardService boardService;

    @Operation(
            summary = "Crear pictograma",
            description = "Crea un nuevo pictograma para el usuario dependiente",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Pictograma creado exitosamente"
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
    @PostMapping("/pictogram")
    public ResponseEntity<?> pushPictogram(
            @RequestHeader("X-User-ID") String userId,
            @RequestBody PictogramPushRequestDto request
    ) {
        PictogramDto response = boardService.pushPictogram(userId, request);
        return ResponseEntity.created(URI.create("/board/pictogram/" + response.id())).body(response);
    }

    @Operation(
            summary = "Actualizar pictograma",
            description = "Actualiza un pictograma existente del usuario dependiente",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Pictograma actualizado exitosamente"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Petición inválida o parámetros incorrectos"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Pictograma o usuario no encontrado"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor"
                    )
            }
    )
    @PutMapping("/pictogram/{id}")
    public ResponseEntity<?> updatePictogram(
            @RequestHeader("X-User-ID") String userId,
            @PathVariable UUID id,
            @RequestBody PictogramUpdateRequestDto request
    ) {
        PictogramDto response = boardService.updatePictogram(userId, id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Eliminar pictograma",
            description = "Elimina un pictograma existente del usuario dependiente",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Pictograma eliminado exitosamente"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Petición inválida o parámetros incorrectos"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Pictograma o usuario no encontrado"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor"
                    )
            }
    )
    @DeleteMapping("/pictogram/{id}")
    public ResponseEntity<?> deletePictogram(
            @RequestHeader("X-User-ID") String userId,
            @PathVariable UUID id,
            @RequestBody DeletePictogramRequestDto request
    ) {
        boardService.deletePictogram(userId, id, request);
        return ResponseEntity.noContent().build();
    }

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
        ImageDto response = boardService.pushImage(userId, request, file);
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
        boardService.deleteImage(userId, id, request);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Obtener todos los pictogramas",
            description = "Obtiene la lista de todos los pictogramas del usuario dependiente especificado",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Obtención exitosa de pictogramas"
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
    @GetMapping("/getAll-pictograms")
    public ResponseEntity<?> getAll(
            @RequestHeader("X-User-ID") String userId,
            @RequestParam("ownerId") UUID ownerId
    ) {
        GetAllPictogramsRequestDto request = new GetAllPictogramsRequestDto(ownerId);
        List<PictogramDto> response = boardService.getAllPictograms(userId, request);
        return ResponseEntity.ok(response);
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
        List<ImageDto> response = boardService.getAllImages(userId, request);
        return ResponseEntity.ok(response);
    }
}