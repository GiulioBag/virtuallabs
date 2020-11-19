package it.polito.ai.virtuallabs.repositories;

import it.polito.ai.virtuallabs.entities.DeliveredPaper;
import it.polito.ai.virtuallabs.entities.Paper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveredPaperRepository  extends JpaRepository <DeliveredPaper, String> {
    List <DeliveredPaper> getAllByPaper(Paper paper);

    List <DeliveredPaper> getAllByPaperOrderByDeliveredDate(Paper paper);
}
