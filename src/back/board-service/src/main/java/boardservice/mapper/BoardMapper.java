package boardservice.mapper;

import boardservice.dto.BoardDto;
import boardservice.dto.BoardFullDto;
import boardservice.dto.ImageSimpleDto;
import boardservice.dto.PictogramPositionBoardDto;
import boardservice.dto.PictogramPositionBoardRequestDto;
import boardservice.dto.PictogramPositionFullDto;
import boardservice.dto.PictogramSimpleDto;
import boardservice.dto.SectionPositionDto;
import boardservice.dto.SectionPositionFullDto;
import boardservice.dto.SectionPositionRequestDto;
import boardservice.entity.Board;
import boardservice.entity.BoardPictogram;
import boardservice.entity.BoardSection;
import boardservice.entity.Language;
import boardservice.entity.Pictogram;
import boardservice.entity.Section;
import boardservice.entity.SectionPictogram;
import boardservice.repository.LanguageRepository;
import boardservice.repository.PictogramRepository;
import boardservice.repository.SectionRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Mapper para convertir entre entidades Board y DTOs.
 */
@Component
public class BoardMapper {
    
    private final SectionRepository sectionRepository;
    private final LanguageRepository languageRepository;
    private final PictogramRepository pictogramRepository;
    
    public BoardMapper(SectionRepository sectionRepository, LanguageRepository languageRepository, PictogramRepository pictogramRepository) {
        this.sectionRepository = sectionRepository;
        this.languageRepository = languageRepository;
        this.pictogramRepository = pictogramRepository;
    }
    
    /**
     * Convierte una entidad Board a BoardDto
     */
    public BoardDto toDto(Board board) {
        List<SectionPositionDto> sectionDtos = board.getSectionPositions().stream()
                .map(this::toSectionPositionDto)
                .toList();
        
        List<PictogramPositionBoardDto> pictogramDtos = board.getPictogramPositions().stream()
                .map(this::toPictogramPositionBoardDto)
                .toList();
        
        return new BoardDto(
                board.getId(),
                board.getOwnerId(),
                board.getLanguage() != null ? board.getLanguage().getCode() : null,
                board.isPublic(),
                sectionDtos,
                pictogramDtos
        );
    }
    
    /**
     * Convierte un BoardSection a SectionPositionDto
     */
    private SectionPositionDto toSectionPositionDto(BoardSection boardSection) {
        Section section = boardSection.getSection();
        String imageUrl = buildImageUrl(section);
        return new SectionPositionDto(
                section.getId(),
                section.getName(),
                boardSection.getCol(),
                boardSection.getRow(),
                imageUrl
        );
    }
    
    /**
     * Construye la URL de imagen en formato data URI desde la entidad Section
     */
    private String buildImageUrl(Section section) {
        if (section.getImage() != null && section.getImage().getImage() != null) {
            String mimeType = section.getImage().getMimeType();
            byte[] imageBytes = section.getImage().getImage();
            String base64 = java.util.Base64.getEncoder().encodeToString(imageBytes);
            return "data:" + mimeType + ";base64," + base64;
        }
        return null;
    }
    
    /**
     * Crea un nuevo Board para un usuario dependiente
     */
    public Board createForUser(UUID ownerId, String languageCode) {
        Language language = languageRepository.findByCode(languageCode)
                .orElseThrow(() -> new IllegalArgumentException("Idioma no encontrado: " + languageCode));
        
        Board board = new Board();
        board.setOwnerId(ownerId);
        board.setLanguage(language);
        board.setPublic(false);
        
        return board;
    }
    
    /**
     * Crea una lista de BoardSection a partir de las solicitudes de posición
     */
    public List<BoardSection> createBoardSections(UUID boardId, List<SectionPositionRequestDto> requests) {
        return requests.stream()
                .map(request -> createBoardSection(boardId, request))
                .toList();
    }
    
    /**
     * Crea un BoardSection a partir de una solicitud
     */
    private BoardSection createBoardSection(UUID boardId, SectionPositionRequestDto request) {
        Section section = sectionRepository.findById(request.sectionId())
                .orElseThrow(() -> new IllegalArgumentException("Sección no encontrada: " + request.sectionId()));
        
        BoardSection boardSection = new BoardSection();
        boardSection.setBoardId(boardId);
        boardSection.setCol(request.col());
        boardSection.setRow(request.row());
        boardSection.setSection(section);
        
        return boardSection;
    }
    
    /**
     * Clona las posiciones de secciones de un tablero público a uno nuevo
     */
    public List<BoardSection> cloneBoardSections(UUID newBoardId, List<BoardSection> sourceSections) {
        return sourceSections.stream()
                .map(source -> {
                    BoardSection clone = new BoardSection();
                    clone.setBoardId(newBoardId);
                    clone.setCol(source.getCol());
                    clone.setRow(source.getRow());
                    clone.setSection(source.getSection());
                    return clone;
                })
                .toList();
    }
    
    /**
     * Convierte un BoardPictogram a PictogramPositionBoardDto
     */
    private PictogramPositionBoardDto toPictogramPositionBoardDto(BoardPictogram boardPictogram) {
        Pictogram pictogram = boardPictogram.getPictogram();
        String imageUrl = buildPictogramImageUrl(pictogram);
        return new PictogramPositionBoardDto(
                pictogram.getId(),
                pictogram.getName(),
                boardPictogram.getCol(),
                boardPictogram.getRow(),
                imageUrl
        );
    }
    
    /**
     * Construye la URL de imagen en formato data URI desde la entidad Pictogram
     */
    private String buildPictogramImageUrl(Pictogram pictogram) {
        if (pictogram.getImage() != null && pictogram.getImage().getImage() != null) {
            String mimeType = pictogram.getImage().getMimeType();
            byte[] imageBytes = pictogram.getImage().getImage();
            String base64 = java.util.Base64.getEncoder().encodeToString(imageBytes);
            return "data:" + mimeType + ";base64," + base64;
        }
        return null;
    }
    
    /**
     * Crea una lista de BoardPictogram a partir de las solicitudes de posición
     */
    public List<BoardPictogram> createBoardPictograms(UUID boardId, List<PictogramPositionBoardRequestDto> requests) {
        return requests.stream()
                .map(request -> createBoardPictogram(boardId, request))
                .toList();
    }
    
    /**
     * Crea un BoardPictogram a partir de una solicitud
     */
    private BoardPictogram createBoardPictogram(UUID boardId, PictogramPositionBoardRequestDto request) {
        Pictogram pictogram = pictogramRepository.findById(request.pictogramId())
                .orElseThrow(() -> new IllegalArgumentException("Pictograma no encontrado: " + request.pictogramId()));
        
        BoardPictogram boardPictogram = new BoardPictogram();
        boardPictogram.setBoardId(boardId);
        boardPictogram.setCol(request.col());
        boardPictogram.setRow(request.row());
        boardPictogram.setPictogram(pictogram);
        
        return boardPictogram;
    }
    
    /**
     * Clona las posiciones de pictogramas de un tablero público a uno nuevo
     */
    public List<BoardPictogram> cloneBoardPictograms(UUID newBoardId, List<BoardPictogram> sourcePictograms) {
        return sourcePictograms.stream()
                .map(source -> {
                    BoardPictogram clone = new BoardPictogram();
                    clone.setBoardId(newBoardId);
                    clone.setCol(source.getCol());
                    clone.setRow(source.getRow());
                    clone.setPictogram(source.getPictogram());
                    return clone;
                })
                .toList();
    }
    
    /**
     * Convierte una entidad Board a BoardFullDto (con pictogramas de secciones incluidos)
     * Este DTO se usa para la vista de "play" donde necesitamos todos los pictogramas.
     */
    public BoardFullDto toFullDto(Board board) {
        List<SectionPositionFullDto> sectionDtos = board.getSectionPositions().stream()
                .map(this::toSectionPositionFullDto)
                .toList();
        
        List<PictogramPositionBoardDto> pictogramDtos = board.getPictogramPositions().stream()
                .map(this::toPictogramPositionBoardDto)
                .toList();
        
        return new BoardFullDto(
                board.getId(),
                board.getOwnerId(),
                board.getLanguage() != null ? board.getLanguage().getCode() : null,
                board.isPublic(),
                sectionDtos,
                pictogramDtos
        );
    }
    
    /**
     * Convierte un BoardSection a SectionPositionFullDto (con pictogramas incluidos)
     */
    private SectionPositionFullDto toSectionPositionFullDto(BoardSection boardSection) {
        Section section = boardSection.getSection();
        String imageUrl = buildImageUrl(section);
        
        // Obtener pictogramas de la sección
        List<PictogramPositionFullDto> pictogramPositions = section.getPictogramPositions().stream()
                .map(this::toPictogramPositionFullDto)
                .toList();
        
        return new SectionPositionFullDto(
                section.getId(),
                section.getName(),
                boardSection.getCol(),
                boardSection.getRow(),
                imageUrl,
                pictogramPositions
        );
    }
    
    /**
     * Convierte un SectionPictogram a PictogramPositionFullDto
     */
    private PictogramPositionFullDto toPictogramPositionFullDto(SectionPictogram sectionPictogram) {
        Pictogram pictogram = sectionPictogram.getPictogram();
        return new PictogramPositionFullDto(
                sectionPictogram.getCol(),
                sectionPictogram.getRow(),
                toPictogramSimpleDto(pictogram)
        );
    }
    
    /**
     * Convierte un Pictogram a PictogramSimpleDto (solo id, name e imagen)
     */
    private PictogramSimpleDto toPictogramSimpleDto(Pictogram pictogram) {
        ImageSimpleDto imageDto = null;
        if (pictogram.getImage() != null) {
            String imageUrl = buildPictogramImageUrl(pictogram);
            imageDto = new ImageSimpleDto(pictogram.getImage().getId(), imageUrl);
        }
        
        return new PictogramSimpleDto(
                pictogram.getId(),
                pictogram.getName(),
                imageDto
        );
    }
}
