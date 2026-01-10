package boardservice.repository;

import boardservice.entity.BoardPictogram;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio para gestionar las relaciones entre tableros y pictogramas.
 */
@Repository
public interface BoardPictogramRepository extends JpaRepository<BoardPictogram, UUID> {

    /**
     * Busca todos los pictogramas asociados a un tablero específico.
     */
    List<BoardPictogram> findAllByBoardId(UUID boardId);

    /**
     * Elimina todos los pictogramas asociados a un tablero.
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM BoardPictogram bp WHERE bp.boardId = :boardId")
    void deleteAllByBoardId(@Param("boardId") UUID boardId);
}
