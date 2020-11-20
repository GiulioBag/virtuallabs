package it.polito.ai.virtuallabs.services;

import it.polito.ai.virtuallabs.dtos.AssignmentDTO;
import it.polito.ai.virtuallabs.dtos.DeliveredPaperDTO;
import it.polito.ai.virtuallabs.dtos.PaperDTO;
import it.polito.ai.virtuallabs.dtos.StudentDTO;
import it.polito.ai.virtuallabs.entities.Teacher;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

public interface AssignmentService {
    List<PaperDTO> getPapersByAssignment(Principal principal, String assignmentId);
    StudentDTO getStudentByPaper(String teacherId, String paperId);
    List<DeliveredPaperDTO> getHistoryByPaper(String paperId, String teacherId);
    void insertAssignment(AssignmentDTO assignmentDTO, String courseName, String teacherId) throws IOException;
    void insertPaper (byte[] image, String paperId, Principal principal);
    DeliveredPaperDTO getLastVersion(String teacherId, String paperId) throws IOException;
    void checkPaper(byte[] image, String teacherId, String paperId) throws IOException;
}
