package it.polito.ai.virtuallabs.repositories;

import it.polito.ai.virtuallabs.entities.JwtBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JwtBlacklistRepository extends JpaRepository <JwtBlacklist, String> {
}
