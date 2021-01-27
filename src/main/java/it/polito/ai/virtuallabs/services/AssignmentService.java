package it.polito.ai.virtuallabs.services;

import it.polito.ai.virtuallabs.dtos.*;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

public interface AssignmentService {
    List<PaperDTO> getPapersByAssignment(Principal principal, String assignmentId);

    TeacherDTO getTeacherByAssignment(Principal principal, String assignmentId);

    StudentDTO getStudentByPaper(String teacherId, String paperId) throws IOException;

    List<DeliveredPaperDTO> getHistoryByPaper(String paperId, String teacherId);

    AssignmentDTO insertAssignment(AssignmentDTO assignmentDTO, String courseName, String teacherId) throws IOException;

    void insertPaper(ContentDTO contentDTO, String paperId, Principal principal) throws IOException;

    DeliveredPaperDTO getLastVersion(String teacherId, String paperId) throws IOException;

    void checkPaper(EvaluatedPaperDTO evaluatedPaperDTO, String teacherId, String paperId) throws IOException;

    AssignmentDTO getAssignmentbyPaper(String paperId, Principal principal);
}
