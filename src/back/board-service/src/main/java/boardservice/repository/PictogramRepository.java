package boardservice.repository;

import boardservice.entity.Pictogram;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PictogramRepository extends JpaRepository<Pictogram, UUID> {
    List<Pictogram> findAllByOwnerId(UUID ownerId);

    Optional<Pictogram> findByIdAndOwnerId(UUID id, UUID ownerId);

    List<Pictogram> findAllByImageIdAndOwnerId(UUID imageId, UUID ownerId);
}
