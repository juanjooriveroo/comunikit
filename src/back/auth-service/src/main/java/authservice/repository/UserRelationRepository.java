package authservice.repository;

import authservice.entity.UserRelation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRelationRepository extends JpaRepository<UserRelation, UUID> {
}
