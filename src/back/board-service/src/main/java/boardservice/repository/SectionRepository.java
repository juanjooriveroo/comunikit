package boardservice.repository;

import boardservice.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SectionRepository extends JpaRepository<Section, UUID> {
    List<Section> findAllByOwnerId(UUID ownerId);

    Optional<Section> findByIdAndOwnerId(UUID id, UUID ownerId);
}
