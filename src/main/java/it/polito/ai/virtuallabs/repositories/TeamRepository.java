package it.polito.ai.virtuallabs.repositories;


import it.polito.ai.virtuallabs.entities.Course;
import it.polito.ai.virtuallabs.entities.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, String> {
    //List <Team> getByMembersContaining(Student s);

    Optional<Team> findByCourseAndId(Course course, String name);

    boolean existsByName(String name);

    Team getByName(String name);
}
