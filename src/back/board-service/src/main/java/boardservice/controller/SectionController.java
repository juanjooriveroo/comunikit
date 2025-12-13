package boardservice.controller;

import boardservice.dto.*;
import boardservice.service.SectionService;
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
        name = "Secciones",
        description = "Endpoints para la gestión de secciones"
)
public class SectionController {
    private final SectionService sectionService;

    @Operation(
            summary = "Crear sección",
            description = "Crea una sección por un nombre y una imagen de presentación",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Sección creada exitosamente"
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
                            responseCode = "500",
                            description = "Error interno del servidor"
                    )
            }
    )
    @PostMapping(value = "/section")
    public ResponseEntity<?> pushImage(
            @RequestHeader("X-User-ID") String userId,
            @RequestBody SectionPushRequestDto request
    ) {
        SectionDto response = sectionService.pushSection(userId, request);
        return ResponseEntity.created(URI.create("/section/" + response.id())).body(response);
    }

    @Operation(
            summary = "Eliminar sección",
            description = "Elimina una sección existente",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Sección eliminada exitosamente"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Petición inválida o parámetros incorrectos"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Usuario o sección no encontrado"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor"
                    )
            }
    )
    @DeleteMapping("/section/{id}")
    public ResponseEntity<?> deleteImage(
            @RequestHeader("X-User-ID") String userId,
            @PathVariable UUID id,
            @RequestBody DeleteSectionRequestDto request
    ) {
        sectionService.deleteSection(userId, id, request);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Obtener todas las secciones de un usuario",
            description = "Obtiene la lista de todas las secciones del usuario dependiente especificado",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Obtención exitosa de secciones"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Petición inválida o parámetros incorrectos"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Usuario o sección no encontrado"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor"
                    )
            }
    )
    @GetMapping("/getAll-section")
    public ResponseEntity<?> getAllImages(
            @RequestHeader("X-User-ID") String userId,
            @RequestParam("ownerId") UUID ownerId
    ) {
        List<SectionDto> response = sectionService.getAllSection(userId, ownerId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Actualizar sección",
            description = "Actualiza una sección existente del usuario dependiente",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Sección actualizada exitosamente"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Petición inválida o parámetros incorrectos"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Sección o usuario no encontrado"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor"
                    )
            }
    )
    @PutMapping("/section/{id}")
    public ResponseEntity<?> updateSection(
            @RequestHeader("X-User-ID") String userId,
            @PathVariable UUID id,
            @RequestBody SectionUpdateRequestDto request
    ) {
        SectionDto response = sectionService.updateSection(userId, id, request);
        return ResponseEntity.ok(response);
    }
}
