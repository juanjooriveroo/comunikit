package boardservice.service;

import boardservice.dto.BoardDto;
import boardservice.dto.BoardUpdateRequestDto;
import boardservice.entity.Board;
import boardservice.entity.BoardPictogram;
import boardservice.entity.BoardSection;
import boardservice.mapper.BoardMapper;
import boardservice.repository.BoardPictogramRepository;
import boardservice.repository.BoardRepository;
import boardservice.repository.BoardSectionRepository;
import boardservice.utils.UserValidator;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Servicio para gestionar tableros de comunicación.
 */
@Service
@RequiredArgsConstructor
public class BoardService {
    
    private final BoardRepository boardRepository;
    private final BoardSectionRepository boardSectionRepository;
    private final BoardPictogramRepository boardPictogramRepository;
    private final BoardMapper boardMapper;
    private final UserValidator userValidator;
    private final EntityManager entityManager;
    
    /**
     * Obtiene el tablero de un usuario dependiente.
     * Valida que el tutor tenga permiso sobre el dependiente.
     * Si el dependiente no tiene tablero, lo crea automáticamente.
     *
     * @param dependentId ID del usuario dependiente
     * @param tutorId ID del tutor que realiza la petición
     * @return DTO del tablero
     */
    @Transactional
    public BoardDto getBoardByDependentId(UUID dependentId, UUID tutorId) {
        userValidator.validateUserAccess(tutorId.toString(), dependentId);
        
        // Buscar tablero existente o crear uno nuevo
        Board board = boardRepository.findByOwnerId(dependentId)
                .orElseGet(() -> {
                    // Crear tablero automáticamente con idioma español por defecto
                    return createBoardForUserInternal(dependentId, "es");
                });
        
        return boardMapper.toDto(board);
    }
    
    /**
     * Obtiene un tablero público por código de idioma.
     *
     * @param languageCode Código del idioma (es, en, fr, etc.)
     * @return DTO del tablero público
     */
    public BoardDto getPublicBoard(String languageCode) {
        Board board = boardRepository.findByLanguageCodeAndIsPublicTrue(languageCode)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No se encontró tablero público para el idioma: " + languageCode
                ));
        
        return boardMapper.toDto(board);
    }
    
    /**
     * Actualiza las secciones y pictogramas de un tablero.
     * Reemplaza todas las posiciones existentes.
     *
     * @param dependentId ID del usuario dependiente propietario del tablero
     * @param tutorId ID del tutor que realiza la petición
     * @param request DTO con las nuevas posiciones de secciones y pictogramas
     * @return DTO del tablero actualizado
     */
    @Transactional
    public BoardDto updateBoard(UUID dependentId, UUID tutorId, BoardUpdateRequestDto request) {
        userValidator.validateUserAccess(tutorId.toString(), dependentId);
        
        Board board = boardRepository.findByOwnerId(dependentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No se encontró tablero para el usuario: " + dependentId
                ));
        
        if (board.isPublic()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No se puede modificar un tablero público"
            );
        }
        
        // Validar que no haya posiciones duplicadas
        validateNoDuplicatePositions(request);
        
        // Limpiar secciones existentes y hacer flush
        board.getSectionPositions().clear();
        board.getPictogramPositions().clear();
        entityManager.flush();
        
        // Crear nuevas posiciones de secciones
        List<BoardSection> newSections = boardMapper.createBoardSections(board.getId(), request.sections());
        board.getSectionPositions().addAll(newSections);
        
        // Crear nuevas posiciones de pictogramas (si hay)
        if (request.pictograms() != null && !request.pictograms().isEmpty()) {
            List<BoardPictogram> newPictograms = boardMapper.createBoardPictograms(board.getId(), request.pictograms());
            board.getPictogramPositions().addAll(newPictograms);
        }
        
        boardRepository.save(board);
        
        return boardMapper.toDto(board);
    }
    
    /**
     * Crea un tablero para un nuevo usuario dependiente.
     * Clona el tablero público del idioma especificado.
     * Este método es llamado desde el consumidor de Kafka.
     *
     * @param dependentId ID del usuario dependiente
     * @param languageCode Código del idioma
     * @return DTO del tablero creado
     */
    @Transactional
    public BoardDto createBoardForUser(UUID dependentId, String languageCode) {
        // Verificar que el usuario no tenga ya un tablero
        if (boardRepository.existsByOwnerId(dependentId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El usuario ya tiene un tablero asignado"
            );
        }
        
        Board newBoard = createBoardForUserInternal(dependentId, languageCode);
        return boardMapper.toDto(newBoard);
    }
    
    /**
     * Método interno para crear tablero. Usado por getBoardByDependentId y createBoardForUser.
     */
    private Board createBoardForUserInternal(UUID dependentId, String languageCode) {
        // Obtener el tablero público del idioma (si no existe, usar uno vacío)
        Board publicBoard = boardRepository.findByLanguageCodeAndIsPublicTrue(languageCode)
                .orElse(null);
        
        // Crear el nuevo tablero
        Board newBoard = boardMapper.createForUser(dependentId, languageCode);
        newBoard = boardRepository.save(newBoard);
        entityManager.flush();
        
        // Clonar las posiciones de secciones del tablero público si existe
        if (publicBoard != null && publicBoard.getSectionPositions() != null 
                && !publicBoard.getSectionPositions().isEmpty()) {
            List<BoardSection> clonedSections = boardMapper.cloneBoardSections(
                    newBoard.getId(),
                    publicBoard.getSectionPositions()
            );
            boardSectionRepository.saveAll(clonedSections);
            entityManager.flush();
        }
        
        // Clonar las posiciones de pictogramas del tablero público si existe
        if (publicBoard != null && publicBoard.getPictogramPositions() != null 
                && !publicBoard.getPictogramPositions().isEmpty()) {
            List<BoardPictogram> clonedPictograms = boardMapper.cloneBoardPictograms(
                    newBoard.getId(),
                    publicBoard.getPictogramPositions()
            );
            boardPictogramRepository.saveAll(clonedPictograms);
            entityManager.flush();
        }
        
        // Limpiar la sesión y recargar el tablero con sus relaciones
        entityManager.clear();
        return boardRepository.findById(newBoard.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Error al crear tablero"
                ));
    }
    
    /**
     * Valida que no haya posiciones duplicadas en la solicitud (secciones + pictogramas)
     */
    private void validateNoDuplicatePositions(BoardUpdateRequestDto request) {
        Set<String> positions = new HashSet<>();
        
        // Agregar posiciones de secciones
        for (var section : request.sections()) {
            String pos = section.col() + "," + section.row();
            if (!positions.add(pos)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "No puede haber elementos en la misma posición: " + pos
                );
            }
        }
        
        // Agregar posiciones de pictogramas
        if (request.pictograms() != null) {
            for (var pictogram : request.pictograms()) {
                String pos = pictogram.col() + "," + pictogram.row();
                if (!positions.add(pos)) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "No puede haber elementos en la misma posición: " + pos
                    );
                }
            }
        }
    }
}
