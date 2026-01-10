package boardservice.repository;

import boardservice.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BoardRepository extends JpaRepository<Board, UUID> {
    
    /**
     * Busca un tablero por su propietario
     */
    Optional<Board> findByOwnerId(UUID ownerId);
    
    /**
     * Busca un tablero público por idioma
     */
    Optional<Board> findByLanguageCodeAndIsPublicTrue(String languageCode);
    
    /**
     * Verifica si existe un tablero para un propietario
     */
    boolean existsByOwnerId(UUID ownerId);
}
