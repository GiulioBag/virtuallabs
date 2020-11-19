package it.polito.ai.virtuallabs.repositories;

import it.polito.ai.virtuallabs.entities.Course;
import it.polito.ai.virtuallabs.entities.Student;
import it.polito.ai.virtuallabs.entities.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface StudentRepository extends JpaRepository<Student, String> {

    Optional<Student> findByUser_SerialNumber(String serialNUmber);
    Student getByUser_SerialNumber(String serialNumber);

    //List<Student> getByCoursesContaining(Course c);

    //List<Student> getByTeamsContaining(Team t);

    //Optional<Student> findByUserId(Long l);
}
