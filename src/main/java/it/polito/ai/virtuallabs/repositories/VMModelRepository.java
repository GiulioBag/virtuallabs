package it.polito.ai.virtuallabs.repositories;

import it.polito.ai.virtuallabs.entities.Course;
import it.polito.ai.virtuallabs.entities.VMModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VMModelRepository extends JpaRepository<VMModel, String>  {
    Optional<VMModel> findByCourse(Course course);
}
