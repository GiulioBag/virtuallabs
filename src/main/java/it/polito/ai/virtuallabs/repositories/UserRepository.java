package it.polito.ai.virtuallabs.repositories;

import it.polito.ai.virtuallabs.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    List<User> findAllByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findAllByRolesEquals(String role);
}
