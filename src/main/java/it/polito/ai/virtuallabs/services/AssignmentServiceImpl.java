package it.polito.ai.virtuallabs.services;

import it.polito.ai.virtuallabs.dtos.*;
import it.polito.ai.virtuallabs.entities.*;
import it.polito.ai.virtuallabs.enums.PaperStatus;
import it.polito.ai.virtuallabs.exceptions.assignmentExceptions.AssignmentNotFoundException;
import it.polito.ai.virtuallabs.exceptions.courseException.CourseNotFoundException;
import it.polito.ai.virtuallabs.exceptions.deliveredPaperException.MissFiledDeliveredPaperException;
import it.polito.ai.virtuallabs.exceptions.deliveredPaperException.WrongStutusDeliveredPaperException;
import it.polito.ai.virtuallabs.exceptions.paperExceptions.PaperNotCheckableException;
import it.polito.ai.virtuallabs.exceptions.studentException.StudentHasNotPaper;
import it.polito.ai.virtuallabs.exceptions.studentException.StudentNotEnrolledToCourseException;
import it.polito.ai.virtuallabs.exceptions.teacherExceptions.PermissionDeniedException;
import it.polito.ai.virtuallabs.exceptions.teacherExceptions.TeacherNotFoundException;
import it.polito.ai.virtuallabs.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
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
    public TeacherDTO getTeacherByAssignment(Principal principal, String assignmentId) {
        if (!assignmentRepository.existsById(assignmentId))
            throw new AssignmentNotFoundException(assignmentId);

        if (principal.getName().startsWith("d")) {
            if (!teacherRepository.existsById(principal.getName()))
                throw new TeacherNotFoundException(principal.getName());
        } else {
            // check if student exists
            utilitsService.checkStudent(principal.getName());
        }

        Teacher teacher = assignmentRepository.getOne(assignmentId).getCreator();
        return modelMapper.map(teacher, TeacherDTO.class);
    }

    @Override
    @PreAuthorize("hasRole('TEACHER')")
    public StudentDTO getStudentByPaper(String teacherId, String paperId) throws IOException {
        Paper paper = utilitsService.checkPaper(paperId);
        utilitsService.checkTeacher(teacherId);
        utilitsService.checkCourseOwner(paper.getAssignment().getCourse().getName(), teacherId);
        Student s = paper.getStudent();
        return utilitsService.fromStudentEntityToDTO(s);
    }

    @Override
    public List<DeliveredPaperDTO> getHistoryByPaper(String paperId, String userId) {
        Paper paper = utilitsService.checkPaper(paperId);
        if (userId.startsWith("d")) {
            utilitsService.checkTeacher(userId);
            utilitsService.checkCourseOwner(paper.getAssignment().getCourse().getName(), userId);
        } else {
            Student student = utilitsService.checkStudent(userId);
            if (!student.getPapers().contains(paper)) {
                throw new StudentHasNotPaper(userId, paperId);
            }
        }
        return paper.getDeliveredPapers()
                .stream()
                .map(i -> {
                    try {
                        return fromEntityToDTO(i);
                    } catch (IOException e) {
                        System.out.println("Errore in lettura dell'immagine: " + i);
                    }
                    return null;
                })
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasRole('TEACHER')")
    public AssignmentDTO insertAssignment(AssignmentDTO assignmentDTO, String courseName, String teacherId) throws IOException {

        Assignment a = null;

        if (!teacherRepository.existsById(teacherId))
            throw new TeacherNotFoundException(teacherId);
        if (!courseRepository.existsById(courseName))
            throw new CourseNotFoundException(courseName);

        Timestamp now = new Timestamp(System.currentTimeMillis());
        assignmentDTO.setReleaseDate(now);

        assignmentDTO.setExpireDate(new Timestamp(System.currentTimeMillis() + 1000 * 60 * 60 * 24));

        Teacher t = teacherRepository.getOne(teacherId);
        List<Course> courses = t.getCourses();

        for (Course c : courses) {

            if (c.getName().equals(courseName)) {

                a = fromDTOToEntity(assignmentDTO);
                a.setCourse(c);
                a.setCreator(t);
                assignmentRepository.save(a);
                assignmentRepository.flush();

                utilitsService.ImgAdd(a.getId(), assignmentDTO.getContent());

                List<Student> students = c.getStudents();
                for (Student s : students) {
                    Paper p = new Paper();
                    p.setAssignment(a);
                    p.setStudent(s);
                    p.setChangeable(true);
                    paperRepository.save(p);

                    DeliveredPaper dp = new DeliveredPaper();
                    dp.setDeliveredDate(now);
                    dp.setStatus(PaperStatus.NULL);
                    dp.setPaper(p);
                    deliveredPaperRepository.save(dp);
                    studentRepository.save(s);
                }
                teacherRepository.save(t);
                courseRepository.save(c);
                break;
            }
        }
        AssignmentDTO assignmentDTO1 = modelMapper.map(a, AssignmentDTO.class);
        assignmentDTO1.setContent(assignmentDTO.getContent());
        return assignmentDTO1;
    }

    @Override
    @PreAuthorize("hasRole('STUDENT')")
    public void insertPaper (ContentDTO contentDTO, String paperId, Principal principal) throws IOException {

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
        PaperStatus status = deliveredPapers.get(deliveredPapers.size()-1).getStatus();
        if(status.equals(PaperStatus.DELIVERED) || status.equals(PaperStatus.NULL)){
            throw new WrongStutusDeliveredPaperException();
        }


        DeliveredPaper deliveredPaper = new DeliveredPaper(PaperStatus.DELIVERED, new Date().getTime(), paper);
        String id;
        do {
            id = UUID.randomUUID().toString();
        } while (deliveredPaperRepository.existsById(id));
        deliveredPaper.setId(id);
        utilitsService.ImgAdd(id, contentDTO.getImage());
        utilitsService.fromImageToPath(contentDTO.getImage(), "deliveredPapers/" + deliveredPaper.getId());
        paper.getDeliveredPapers().add(deliveredPaper);
        deliveredPaperRepository.save(deliveredPaper);
        deliveredPaperRepository.flush();
        paperRepository.save(paper);

    }

    Assignment fromDTOToEntity(AssignmentDTO dto) throws IOException {
        Assignment a = new Assignment();
        String id;
        do {
            id = UUID.randomUUID().toString();
        }while(assignmentRepository.existsById(id));
        a.setId(id);
        a.setReleaseDate(dto.getReleaseDate());
        a.setExpireDate(dto.getExpireDate());
        a.setName(dto.getName());
        utilitsService.fromImageToPath(dto.getContent(), "/assignments/" + id);
        return a;
    }

    DeliveredPaper fromDTOToEntity(DeliveredPaperDTO dto) throws IOException {
        DeliveredPaper dp = new DeliveredPaper();
        String id;
        do {
            id = UUID.randomUUID().toString();
        }while(deliveredPaperRepository.existsById(id));
        dp.setId(id);
        dp.setStatus(dto.getStatus());
        dp.setDeliveredDate(dto.getDeliveredDate());
        if(dto.getStatus() == PaperStatus.CHECKED || dp.getStatus() == PaperStatus.DELIVERED){
            utilitsService.fromImageToPath(dto.getImage(), "/deliveredPapers/" + id);
        }
        return dp;
    }

    DeliveredPaperDTO fromEntityToDTO(DeliveredPaper dp) throws IOException {
        DeliveredPaperDTO dto = new DeliveredPaperDTO();
        dto.setId(dp.getId());
        dto.setDeliveredDate(dp.getDeliveredDate());
        dto.setStatus(dp.getStatus());
        dto.setComment(dp.getComment());
        if(dp.getStatus() == PaperStatus.CHECKED || dp.getStatus() == PaperStatus.DELIVERED)
            dto.setImage(utilitsService.fromPathToImage("deliveredPapers/" + dto.getId()));
        else
            dto.setImage(null);
        return dto;
    }

    @Override
    public DeliveredPaperDTO getLastVersion(String teacherId, String paperId) throws IOException {
        utilitsService.checkTeacher(teacherId);
        Paper p = utilitsService.checkPaper(paperId);
        utilitsService.checkCourseOwner(p.getAssignment().getCourse().getName(), teacherId);
        return fromEntityToDTO(deliveredPaperRepository.getAllByPaperOrderByDeliveredDate(p).get(deliveredPaperRepository.getAllByPaperOrderByDeliveredDate(p).size()-1));
    }

    @Override
    public void checkPaper(EvaluatedPaperDTO evaluatedPaperDTO, String teacherId, String paperId) throws IOException {
        utilitsService.checkTeacher(teacherId);
        Paper p = utilitsService.checkPaper(paperId);
        utilitsService.checkCourseOwner(p.getAssignment().getCourse().getName(), teacherId);
        if (deliveredPaperRepository.getAllByPaperOrderByDeliveredDate(p).get(deliveredPaperRepository.getAllByPaperOrderByDeliveredDate(p).size() - 1).getStatus() != PaperStatus.DELIVERED)
            throw new PaperNotCheckableException();
        DeliveredPaper dp = new DeliveredPaper();
        dp.setStatus(PaperStatus.CHECKED);
        dp.setDeliveredDate(new Timestamp(System.currentTimeMillis()));
        dp.setPaper(p);
        dp.setComment(evaluatedPaperDTO.getComment());
        String id;
        do {
            id = UUID.randomUUID().toString();
        } while (deliveredPaperRepository.existsById(id));
        dp.setId(id);

        utilitsService.ImgAdd(id, evaluatedPaperDTO.getImg());
        utilitsService.fromImageToPath(evaluatedPaperDTO.getImg(), "/deliveredPapers/" + id);

        if (evaluatedPaperDTO.isAccepted()) {
            p.setChangeable(false);
            p.setScore(evaluatedPaperDTO.getScore());
        }
        deliveredPaperRepository.save(dp);
        paperRepository.save(p);
    }

    @Override
    public AssignmentDTO getAssignmentbyPaper(String paperId, Principal principal) {

        Paper paper = utilitsService.checkPaper(paperId);

        if (principal.getName().startsWith("d")) {
            Teacher teacher = utilitsService.checkTeacher(principal.getName());
            if (!teacher.getCourses().contains(paper.getAssignment().getCourse())) {
                throw new PermissionDeniedException();
            }
        } else {
            Student student = utilitsService.checkStudent(principal.getName());
            if (!student.getPapers().contains(paper)) {
                throw new PermissionDeniedException();
            }
        }
        return modelMapper.map(paper.getAssignment(), AssignmentDTO.class);
    }

}
