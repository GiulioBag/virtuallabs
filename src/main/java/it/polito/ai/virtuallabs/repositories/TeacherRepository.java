package it.polito.ai.virtuallabs.repositories;

import it.polito.ai.virtuallabs.entities.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, String> {

}
