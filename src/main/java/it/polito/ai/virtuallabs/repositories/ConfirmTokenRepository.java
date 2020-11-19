package it.polito.ai.virtuallabs.repositories;

import it.polito.ai.virtuallabs.entities.ConfirmToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfirmTokenRepository extends JpaRepository<ConfirmToken, String> {
}
