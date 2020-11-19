package it.polito.ai.virtuallabs.services;

import com.fasterxml.jackson.annotation.OptBoolean;
import it.polito.ai.virtuallabs.dtos.AssignmentDTO;
import it.polito.ai.virtuallabs.dtos.DeliveredPaperDTO;
import it.polito.ai.virtuallabs.dtos.PaperDTO;
import it.polito.ai.virtuallabs.dtos.StudentDTO;
import it.polito.ai.virtuallabs.entities.*;
import it.polito.ai.virtuallabs.enums.PaperStatus;
import it.polito.ai.virtuallabs.exceptions.assignmentExceptions.AssignmentNotFoundException;
import it.polito.ai.virtuallabs.exceptions.assignmentExceptions.ExpiredAssignmentException;
import it.polito.ai.virtuallabs.exceptions.courseException.CourseNotFoundException;
import it.polito.ai.virtuallabs.exceptions.deliveredPaperException.MissFiledDeliveredPaperException;
import it.polito.ai.virtuallabs.exceptions.deliveredPaperException.WrongStutusDeliveredPaperException;
import it.polito.ai.virtuallabs.exceptions.paperExceptions.NotChangeablePaperException;
import it.polito.ai.virtuallabs.exceptions.paperExceptions.PaperNotFoundException;
import it.polito.ai.virtuallabs.exceptions.studentException.StudentNotEnrolledToCourseException;
import it.polito.ai.virtuallabs.exceptions.studentException.StudentNotFoundException;
import it.polito.ai.virtuallabs.exceptions.teacherExceptions.PermissionDeniedException;
import it.polito.ai.virtuallabs.exceptions.teacherExceptions.TeacherNotFoundException;
import it.polito.ai.virtuallabs.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AssignmentServiceImpl implements AssignmentService{

    @Autowired
    AssignmentRepository assignmentRepository;
    @Autowired
    PaperRepository paperRepository;
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    TeacherRepository teacherRepository;
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    DeliveredPaperRepository deliveredPaperRepository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    UtilitsService utilitsService;


    @Override
    public List<PaperDTO> getPapersByAssignment(Principal principal, String assignmentId) {
        if (!assignmentRepository.existsById(assignmentId))
            throw new AssignmentNotFoundException(assignmentId);

        if (principal.getName().startsWith("d")) {
            if (!teacherRepository.existsById(principal.getName()))
                throw new TeacherNotFoundException(principal.getName());
            List<Teacher> teachers = assignmentRepository.getOne(assignmentId).getCourse().getOwners();
            if (teachers.stream().map(Teacher::getId).collect(Collectors.toList()).contains(principal.getName())) {
                return assignmentRepository.getOne(assignmentId).getPapers()
                        .stream()
                        .map(i -> modelMapper.map(i, PaperDTO.class))
                        .collect(Collectors.toList());
            }
            throw new PermissionDeniedException();

        } else {

            // check if student exists
            Student student = utilitsService.checkStudent(principal.getName());

            // check if the assignment exists
            Assignment assignment = utilitsService.checkAssignment(assignmentId);

            // TODO uno studente ha un solo paper per ogni assignment, non una lista
            List <PaperDTO> paperDTOS = new ArrayList<>();
            paperDTOS.add(modelMapper.map(paperRepository.getByStudentAndAssignment(student, assignment), PaperDTO.class));
            return paperDTOS;
        }
    }

    @Override
    @PreAuthorize("hasRole('TEACHER')")
    public StudentDTO getStudentByPaper(String teacherId, String paperId) {
        Paper paper = utilitsService.checkPaper(paperId);
        utilitsService.checkTeacher(teacherId);
        utilitsService.checkCourseOwner(paper.getAssignment().getCourse().getName(), teacherId);

        return modelMapper.map(paper.getStudent(), StudentDTO.class);
    }

    @Override
    @PreAuthorize("hasRole('TEACHER')")
    public List<DeliveredPaperDTO> getHistoryByPaper(String paperId, String teacherId) {
        Paper paper = utilitsService.checkPaper(paperId);
        utilitsService.checkTeacher(teacherId);
        utilitsService.checkCourseOwner(paper.getAssignment().getCourse().getName(), teacherId);

        return paper.getDeliveredPapers()
                .stream()
                .map(i -> modelMapper.map(i, DeliveredPaperDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasRole('TEACHER')")
    public void insertAssignment(AssignmentDTO assignmentDTO, String courseName, String teacherId){

        if(!teacherRepository.existsById(teacherId))
            throw new TeacherNotFoundException(teacherId);
        if(!courseRepository.existsById(courseName))
            throw new CourseNotFoundException(courseName);

        Timestamp now = new Timestamp(System.currentTimeMillis());
        Teacher t = teacherRepository.getOne(teacherId);
        List<Course> courses = t.getCourses();
        for (Course c : courses) {
            if (c.getName().equals(courseName)) {
                Assignment a = modelMapper.map(assignmentDTO, Assignment.class);
                a.setCourse(c);
                a.setCreator(t);
                a.setReleaseDate(now);
                List<Student> students = c.getStudents();
                for (Student s : students){
                    Paper p = new Paper();
                    p.setAssignment(a);
                    p.setStudent(s);
                    p.setChangeable(true);

                    DeliveredPaper dp = new DeliveredPaper();
                    dp.setDeliveredDate(now);
                    dp.setStatus(PaperStatus.NULL);
                    dp.setPaper(p);

                    studentRepository.save(s);
                    paperRepository.save(p);
                    deliveredPaperRepository.save(dp);
                }
                assignmentRepository.save(a);
                teacherRepository.save(t);
                courseRepository.save(c);
                return;
            }
        }
    }

    @Override
    @PreAuthorize("hasRole('STUDENT')")
    public void insertPaper (DeliveredPaperDTO deliveredPaperDTO, String paperId, Principal principal){

        // check if the student exists
        Student student = utilitsService.checkStudent(principal.getName());

        // check if the paper exists
        if(paperId == null){
            throw new MissFiledDeliveredPaperException();
        }
        Paper paper = utilitsService.checkPaper(paperId);

        // check if the assignment exists
        Assignment assignment = utilitsService.checkAssignment(paper.getAssignment().getId());

        // changes paper's state if its assignment is expired
        utilitsService.checkExpiredAssignmentByPaper(paper, true);

        // check if the student is enrolled to the assignment's course
        if(!student.getCourses().contains(assignment.getCourse())){
            throw new StudentNotEnrolledToCourseException(student.getId(), assignment.getCourse().getName());
        }

        // check if the delivered paper is not already inserted
        List<DeliveredPaper> deliveredPapers = deliveredPaperRepository.getAllByPaperOrderByDeliveredDate(paper);
        PaperStatus status = deliveredPapers.get(deliveredPapers.size() -1 ).getStatus();
        if(status.equals(PaperStatus.DELIVERED) || status.equals(PaperStatus.NULL)){
            throw new WrongStutusDeliveredPaperException();
        }

        DeliveredPaper deliveredPaper = new DeliveredPaper(PaperStatus.DELIVERED, new Date().getTime(), null, paper);
        paper.getDeliveredPapers().add(deliveredPaper);
        paperRepository.save(paper);
        deliveredPaperRepository.save(deliveredPaper);
    }

}
