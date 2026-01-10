package boardservice.repository;

import boardservice.entity.BoardSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio para gestionar las relaciones entre tableros y secciones.
 */
public interface BoardSectionRepository extends JpaRepository<BoardSection, UUID> {
    
    /**
     * Obtiene todas las posiciones de secciones de un tablero
     */
    List<BoardSection> findAllByBoardId(UUID boardId);
    
    /**
     * Elimina todas las posiciones de secciones de un tablero
     */
    @Modifying
    @Query("DELETE FROM BoardSection bs WHERE bs.boardId = :boardId")
    void deleteAllByBoardId(@Param("boardId") UUID boardId);
}
