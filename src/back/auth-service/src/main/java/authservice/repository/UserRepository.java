package authservice.repository;

import authservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    List<User> findByUsername(String username);

    long countUserByUsername(String username);

    long countUserByUsernameStartingWith(String username);
}
