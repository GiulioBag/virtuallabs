package it.polito.ai.virtuallabs.repositories;

import it.polito.ai.virtuallabs.entities.VM;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VMRepository extends JpaRepository <VM, String> {
}
