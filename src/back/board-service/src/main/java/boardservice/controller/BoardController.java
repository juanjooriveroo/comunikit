package boardservice.controller;

import boardservice.dto.BoardDto;
import boardservice.dto.BoardFullDto;
import boardservice.dto.BoardUpdateRequestDto;
import boardservice.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Tag(name = "Board", description = "Gestión de tableros de comunicación")
public class BoardController {
    private final BoardService boardService;
    
    @Operation(
            summary = "Obtener tablero de usuario",
            description = "Obtiene el tablero de comunicación de un usuario dependiente. " +
                    "El tutor autenticado debe tener relación con el dependiente."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Tablero obtenido correctamente",
                    content = @Content(schema = @Schema(implementation = BoardDto.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Sin permiso para acceder al tablero del dependiente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Tablero no encontrado"
            )
    })
    @GetMapping("/board/{dependentId}")
    public ResponseEntity<BoardDto> getBoard(
            @Parameter(description = "ID del usuario dependiente", required = true)
            @PathVariable UUID dependentId,
            @Parameter(description = "ID del tutor autenticado", required = true)
            @RequestHeader("X-User-Id") UUID tutorId
    ) {
        BoardDto response = boardService.getBoardByDependentId(dependentId, tutorId);
        return ResponseEntity.ok(response);
    }
    
    @Operation(
            summary = "Obtener tablero completo de usuario para Play",
            description = "Obtiene el tablero completo con todos los pictogramas de las secciones. " +
                    "Se usa para la vista de 'play' donde se necesitan todos los pictogramas."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Tablero completo obtenido correctamente",
                    content = @Content(schema = @Schema(implementation = BoardFullDto.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Sin permiso para acceder al tablero del dependiente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Tablero no encontrado"
            )
    })
    @GetMapping("/board-full/{dependentId}")
    public ResponseEntity<BoardFullDto> getBoardFull(
            @Parameter(description = "ID del usuario dependiente", required = true)
            @PathVariable UUID dependentId
    ) {
        BoardFullDto response = boardService.getBoardFullByDependentId(dependentId);
        return ResponseEntity.ok(response);
    }
    
    @Operation(
            summary = "Obtener tablero público",
            description = "Obtiene el tablero público (plantilla) de un idioma específico. " +
                    "No requiere autenticación. Solo devuelve información básica de secciones."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Tablero público obtenido correctamente",
                    content = @Content(schema = @Schema(implementation = BoardDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No existe tablero público para el idioma especificado"
            )
    })
    @GetMapping("/board-public/{languageCode}")
    public ResponseEntity<BoardDto> getPublicBoard(
            @Parameter(description = "Código del idioma (es, en, fr, de, pt)", required = true)
            @PathVariable String languageCode
    ) {
        BoardDto response = boardService.getPublicBoard(languageCode);
        return ResponseEntity.ok(response);
    }
    
    @Operation(
            summary = "Obtener tablero público completo para Play",
            description = "Obtiene el tablero público completo con todos los pictogramas de las secciones. " +
                    "Se usa para la vista de 'play' de invitados donde se necesitan todos los pictogramas. " +
                    "No requiere autenticación."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Tablero público completo obtenido correctamente",
                    content = @Content(schema = @Schema(implementation = BoardFullDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No existe tablero público para el idioma especificado"
            )
    })
    @GetMapping("/board-public-full/{languageCode}")
    public ResponseEntity<BoardFullDto> getPublicBoardFull(
            @Parameter(description = "Código del idioma (es, en, fr, de, pt)", required = true)
            @PathVariable String languageCode
    ) {
        BoardFullDto response = boardService.getPublicBoardFull(languageCode);
        return ResponseEntity.ok(response);
    }
    
    @Operation(
            summary = "Actualizar tablero",
            description = "Actualiza las secciones posicionadas en el tablero de un usuario dependiente. " +
                    "Reemplaza todas las posiciones existentes."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Tablero actualizado correctamente",
                    content = @Content(schema = @Schema(implementation = BoardDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos o posiciones duplicadas"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Sin permiso para modificar el tablero o es un tablero público"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Tablero o sección no encontrada"
            )
    })
    @PutMapping("/board/{dependentId}")
    public ResponseEntity<BoardDto> updateBoard(
            @Parameter(description = "ID del usuario dependiente", required = true)
            @PathVariable UUID dependentId,
            @Parameter(description = "ID del tutor autenticado", required = true)
            @RequestHeader("X-User-Id") UUID tutorId,
            @Valid @RequestBody BoardUpdateRequestDto request
    ) {
        BoardDto response = boardService.updateBoard(dependentId, tutorId, request);
        return ResponseEntity.ok(response);
    }
}