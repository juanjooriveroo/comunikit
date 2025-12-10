package boardservice.controller;

import boardservice.dto.DeletePictogramRequestDto;
import boardservice.dto.GetAllPictogramsRequestDto;
import boardservice.dto.PictogramDto;
import boardservice.dto.PictogramPushRequestDto;
import boardservice.dto.PictogramUpdateRequestDto;
import boardservice.service.PictogramService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Tag(
        name = "Pictogramas",
        description = "Endpoints para la gestión de pictogramas"
)
public class PictogramController {

    private final PictogramService pictogramService;

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
        PictogramDto response = pictogramService.pushPictogram(userId, request);
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
        PictogramDto response = pictogramService.updatePictogram(userId, id, request);
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
        pictogramService.deletePictogram(userId, id, request);
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
        List<PictogramDto> response = pictogramService.getAllPictograms(userId, request);
        return ResponseEntity.ok(response);
    }
}
