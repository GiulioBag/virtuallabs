package it.polito.ai.virtuallabs.repositories;


import it.polito.ai.virtuallabs.entities.Course;
import it.polito.ai.virtuallabs.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface CourseRepository extends JpaRepository<Course, String> {

    @Query("SELECT c FROM  Course c INNER JOIN c.owners t WHERE t.id=:teacherId")
    List<Course> getCoursesByTeacher(String teacherId);

    Optional<Course> findByName (String name);

    @Query("SELECT s1 FROM Student s1 INNER JOIN s1.courses  cs WHERE cs.name=:courseName AND s1.id IN (" +
            " SELECT s.id FROM Student s INNER JOIN s.teams t INNER JOIN t.course c WHERE c.name=:courseName)")
    List<Student> getStudentsInTeams(String courseName);

    @Query("SELECT s1 FROM Student s1 INNER JOIN s1.courses  cs WHERE cs.name=:courseName AND s1.id NOT IN (" +
            " SELECT s.id FROM Student s INNER JOIN s.teams t INNER JOIN t.course c WHERE c.name=:courseName)")
    List<Student> getStudentsNotInTeams(String courseName);

   /*
    List<Course> getByStudentsContaining(Student s);
    @Query("SELECT s FROM Student s INNER JOIN s.teams t INNER JOIN t.course c WHERE c.name=:courseName")
    List <Student> getStudentsInTeams(String courseName);

     */
}