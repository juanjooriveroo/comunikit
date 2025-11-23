package authservice.repository;

import authservice.entity.User;
import authservice.entity.UserRelation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRelationRepository extends JpaRepository<UserRelation, UUID> {
    void deleteByUser(User user);
    void deleteByTutor(User tutor);
}
