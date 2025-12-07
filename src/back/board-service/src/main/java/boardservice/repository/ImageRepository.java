package boardservice.repository;

import boardservice.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, UUID> {
    Optional<Image> findByIdAndOwnerId(UUID id, UUID ownerId);

    List<Image> findAllByOwnerId(UUID ownerId);
}
