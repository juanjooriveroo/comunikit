package boardservice.repository;

import boardservice.entity.Pictogram;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PictogramRepository extends JpaRepository<Pictogram, UUID> {
    List<Pictogram> findAllByOwnerId(UUID ownerId);

    Iterable<Object> findAllByOwnerIdOrderByName(UUID ownerId);

    Iterable<Object> findAllByOwnerIdOrderByNameAsc(UUID ownerId);
}
