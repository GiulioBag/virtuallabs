package it.polito.ai.virtuallabs.repositories;

import it.polito.ai.virtuallabs.entities.Team;
import it.polito.ai.virtuallabs.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String>  {
    List<User> findAllByEmail (String email);
    boolean existsByEmail(String email);
}
