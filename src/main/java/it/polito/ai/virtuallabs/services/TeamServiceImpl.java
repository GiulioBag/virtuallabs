package it.polito.ai.virtuallabs.services;

import it.polito.ai.virtuallabs.dtos.ProposedTeamDTO;
import it.polito.ai.virtuallabs.dtos.StudentDTO;
import it.polito.ai.virtuallabs.dtos.VMDTO;
import it.polito.ai.virtuallabs.entities.Course;
import it.polito.ai.virtuallabs.entities.Student;
import it.polito.ai.virtuallabs.entities.Teacher;
import it.polito.ai.virtuallabs.entities.Team;
import it.polito.ai.virtuallabs.exceptions.courseException.CourseNotFoundException;
import it.polito.ai.virtuallabs.exceptions.studentException.*;
import it.polito.ai.virtuallabs.exceptions.teacherExceptions.PermissionDeniedException;
import it.polito.ai.virtuallabs.exceptions.teamException.*;
import it.polito.ai.virtuallabs.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TeamServiceImpl implements  TeamService {


    @Autowired
    ModelMapper modelMapper;
    @Autowired
    TeamRepository teamRepository;
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    VMRepository vmRepository;
    @Autowired
    UtilitsService utilitsService;

    @Override
    @PreAuthorize("hasRole('STUDENT')")
    public List<StudentDTO> getTeamStudentByTeam(String teamId, Principal principal) throws IOException {

        // check if team exists
        Team team = utilitsService.checkTeam(teamId);

        // check if the student is in the team
        Student student = studentRepository.getByUser_SerialNumber(principal.getName());
        if (!team.getStudents().contains(student) && !team.getWaitingStudents().contains(student)) {
            throw new StudentNotBelongToTeam(principal.getName(), teamId);
        }

        // check if the team is not expired
        utilitsService.checkTeamExpired(team, true);

        List<StudentDTO> studentDTOList = new ArrayList<>();

        for (Student teamStudent : team.getStudents()) {
            studentDTOList.add(utilitsService.fromStudentEntityToDTO(teamStudent));
        }

        return studentDTOList;

    }

    // create a propose for a new team
    @Override
    @PreAuthorize("hasRole('STUDENT')")
    public void proposeTeam (ProposedTeamDTO proposedTeamDTO, String courseName, Principal principal) {

        // check if team's course exist
        Optional<Course> optCourse = courseRepository.findByName(courseName);
        if(optCourse.isEmpty()){
            throw new CourseNotFoundException(courseName);
        }

        // check if already exist a team with the same name and same course
        Course course = optCourse.get();
        Optional<Team> optTeam = teamRepository.findByCourseAndId(course, proposedTeamDTO.getName());
        if(optTeam.isPresent()){
            throw new TeamAlreadyExistException(proposedTeamDTO.getName(), courseName);
        }

        // check if the dimension of the proposed team are correct
        int numStudentd = proposedTeamDTO.getStudentIds().size() + 1;
        if (!(course.getMinGroupSize() <= numStudentd && course.getMaxGroupSize() >= numStudentd)){
            throw  new WrongTeamDimensionException(course.getMinGroupSize(), course.getMaxGroupSize(), numStudentd);
        }

        // check if there are not duplicated student
        int numNoDuplicate = new HashSet<>(proposedTeamDTO.getStudentIds()).size() + 1;
        if(numStudentd != numNoDuplicate){
            throw  new DuplicatedTeamMembersException();
        }

        // check if the student who proposes the team is not inside the student list
        if(proposedTeamDTO.getStudentIds().contains(principal.getName())){
            throw new CreatorInsideTeamException();
        }

        List <Student> waitingStudents = new ArrayList<>();

        // check if students of proposed teams exist and that they are enrolled to the course
        for (String serialNumber: proposedTeamDTO.getStudentIds()){
            Optional<Student> optStudent = studentRepository.findByUser_SerialNumber(serialNumber);
            if(optStudent.isEmpty()){
                throw new StudentNotFoundException(serialNumber);
            }

            Student student = optStudent.get();
            if(!(student.getCourses().contains(course))){
                throw new StudentNotEnrolledToCourseException(serialNumber, courseName);
            }

            waitingStudents.add(student);

        }
        // check if students not belong to another team of the same course
        List<Student> studentsAlreadyInTeam = courseRepository.getStudentsInTeams(courseName);
        for(Student student: studentsAlreadyInTeam){
            String serialNumber = student.getUser().getSerialNumber();
            if(proposedTeamDTO.getStudentIds().contains(serialNumber)){
                throw new StudentAlreadyInTeamException(serialNumber, courseName);
            }
        }

        Team team = new Team();
        team.setName(proposedTeamDTO.getName());
        team.setCourse(course);
        for (Student auxStudent:waitingStudents){
            team.addWaitingStudent(auxStudent);
            studentRepository.save(auxStudent);
        }
        Student owner = studentRepository.getByUser_SerialNumber(principal.getName());
        team.addStudent(owner);
        studentRepository.save(owner);
        team.setTimeout(new Timestamp(proposedTeamDTO.getTimeout().getTime()));
        teamRepository.save(team);
    }


    @Override
    @PreAuthorize("hasRole('STUDENT')")
    public void confirmTeamParticipation(String teamID, Principal principal) {

        // TODO potremmo mettere che se lo studente è l'ultimo ad accettare viene inviata una mail
        // TODO inviamo la mail anche nel caso in cui il corso è rifiutato

        // check if team exosts
        Team team = utilitsService.checkTeam(teamID);

        // check if principal is belong to the team
        Student student = utilitsService.checkStudent(principal.getName());
        if(!team.getWaitingStudents().contains(student)){
            throw new StudentNotInvitedToTeamException(student.getId(), teamID);
        }
        student.removeProposedTeam(team);

        if(team.getStudents().contains(student)){
            throw new StudentAlreadyAcceptedTeamException(student.getId(), teamID);
        }


        // check if principal already belongs to a team of the same course
        if(student.getCourses().contains(team.getCourse())) {
            // remove team
            utilitsService.removeTeam(team);
            throw new StudentAlreadyInTeamException(principal.getName(), team.getCourse().getName());
        }

        // check if team is expired
        utilitsService.checkTeamExpired(team, true);

        student.addTeam(team);
        if (team.getWaitingStudents().size() == 0) {
            team.setActive(true);
        }

        studentRepository.save(student);
        teamRepository.save(team);
    }


    @Override
    @PreAuthorize("hasRole('STUDENT')")
    public void rejectTeamParticipation(String teamID, Principal principal) {

        // check if team exist
        Team team = utilitsService.checkTeam(teamID);

        // check if principal is belong to the team
        Student student = utilitsService.checkStudent(principal.getName());
        if(!team.getWaitingStudents().contains(student)){
            throw new StudentNotInvitedToTeamException(student.getId(), teamID);
        }
        // remove team
        utilitsService.removeTeam(team);
    }

    @Override
    @PreAuthorize("hasRole('TEACHER')")
    public List<VMDTO> getVMsByTeam(String teamId, Principal principal) {

        Team team = utilitsService.checkTeam(teamId);

        // check if team is expired
        utilitsService.checkTeamExpired(team, true);

        // check if team is active
        utilitsService.checkTeamActive(team);

        if(principal.getName().startsWith("s")){
            Student student = utilitsService.checkStudent(principal.getName());
            if(!student.getTeams().contains(team)){
                throw new StudentNotBelongToTeam(principal.getName(), teamId);
            }
        } else {
            Teacher teacher = utilitsService.checkTeacher(principal.getName());
            boolean owned = false;
            boolean flag = false;
            for (Course cours : teacher.getCourses()) {
                for (Team coursTeam : cours.getTeams()) {
                    if(coursTeam.equals(team)){
                        owned = true;
                        flag = true;
                        break;
                    }
                }
                if (flag) {
                    break;
                }
            }

            if(!owned){
                throw new PermissionDeniedException();
            }
        }

        return team.getVMs().stream().map(i -> modelMapper.map(i, VMDTO.class)).collect(Collectors.toList());
    }






}
