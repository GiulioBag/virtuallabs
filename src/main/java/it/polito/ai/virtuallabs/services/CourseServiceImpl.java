package it.polito.ai.virtuallabs.services;

import it.polito.ai.virtuallabs.dtos.*;
import it.polito.ai.virtuallabs.entities.*;
import it.polito.ai.virtuallabs.enums.PaperStatus;
import it.polito.ai.virtuallabs.enums.VmState;
import it.polito.ai.virtuallabs.exceptions.courseException.CourseNotFoundException;
import it.polito.ai.virtuallabs.exceptions.studentException.StudentNotEnrolledToCourseException;
import it.polito.ai.virtuallabs.exceptions.studentException.StudentNotFoundException;
import it.polito.ai.virtuallabs.exceptions.studentException.StudentNotHasTeamInCourseException;
import it.polito.ai.virtuallabs.exceptions.teacherExceptions.PermissionDeniedException;
import it.polito.ai.virtuallabs.exceptions.teacherExceptions.TeacherNotFoundException;
import it.polito.ai.virtuallabs.exceptions.vmModelExceptions.VMModelExcessiveLimitsException;
import it.polito.ai.virtuallabs.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CourseServiceImpl implements CourseService{

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    StudentRepository studentRepository;
    @Autowired
    TeacherRepository teacherRepository;
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    TeamRepository teamRepository;
    @Autowired
    VMModelRepository vmModelRepository;
    @Autowired
    AssignmentRepository assignmentRepository;
    @Autowired
    PaperRepository paperRepository;
    @Autowired
    DeliveredPaperRepository deliveredPaperRepository;

    @Autowired
    TeacherService teacherService;

    @Autowired
    UtilitsService utilitsService;

    @Override
    @PreAuthorize("hasRole('STUDENT')")
    public List<TeamDTO> getProposedTeamByCourse(String courseName, Principal principal){

        // check if the course exist
        Optional<Course> optCourse = courseRepository.findByName(courseName);
        if(optCourse.isEmpty()){
            throw new CourseNotFoundException(courseName);
        }

        // check if the student is enrolled to the course
        Student student = utilitsService.checkStudent(principal.getName());
        Course course = optCourse.get();
        if(!course.getStudents().contains(student)){
            throw new StudentNotEnrolledToCourseException(student.getId(), courseName);
        }

        List <Team> proposedTeams = student.getProposedTeams();
        List<TeamDTO> teamDTOList = new ArrayList<>();
        for (Team team: proposedTeams){

            if(team.getCourse().getName().equals(courseName)){

                // check if the propose is expired, if it is the team is removed from db
                utilitsService.checkTeamExpired(team, false);

                // create TeamDTO
                TeamDTO teamDTO = new TeamDTO(team);
                teamDTOList.add(modelMapper.map(team, TeamDTO.class));
            }
        }
        return teamDTOList;
    }

    @Override
    @PreAuthorize("hasRole('STUDENT')")
    public List<StudentDTO> possibleTeamMember(String courseName, Principal principal) {

        // check if the course exist
        Optional<Course> optCourse = courseRepository.findByName(courseName);
        if(optCourse.isEmpty()){
            throw new CourseNotFoundException(courseName);
        }

        // check if the student is enrolled to the course
        Student student = studentRepository.getByUser_SerialNumber(principal.getName());
        Course course = optCourse.get();
        if(!course.getStudents().contains(student)){
            throw new StudentNotEnrolledToCourseException(student.getId(), courseName);
        }

        List<StudentDTO> returnStudentsDTO = new ArrayList<>();
        for (Student auxStudent : courseRepository.getStudentsNotInTeams(courseName)) {
            returnStudentsDTO.add(new StudentDTO(auxStudent));
        }

        return returnStudentsDTO;

    }

    @Override
    @PreAuthorize("hasRole('STUDENT')")
    public Optional<TeamDTO> getTeamByCourse(String courseName, Principal principal) {

        // check if the course exist
        Course course = utilitsService.checkCourse(courseName);

        // check if the student is enrolled to the course
        Student student = utilitsService.checkStudent(principal.getName());

        if (!course.getStudents().contains(student)) {
            throw new StudentNotEnrolledToCourseException(student.getId(), courseName);
        }

        List<Team> teams = student.getTeams();
        TeamDTO teamDTO = null;
        for (Team team : teams) {
            if (team.getCourse().getName().equals(courseName)) {
                // check if the propose is expired, if it is the team is removed from db
                utilitsService.checkTeamExpired(team, false);
                // create TeamDTO
                teamDTO = new TeamDTO(team);
            }
        }

        if(teamDTO == null){
            return Optional.empty();
        } else {
            return Optional.of(teamDTO);
        }
    }

    @Override
    public List<TeamDTO> getTeamsByCourse(String courseName, String teacherId) {
        if(!teacherRepository.existsById(teacherId))
            throw new TeacherNotFoundException(teacherId);
        if(!courseRepository.existsById(courseName))
            throw new CourseNotFoundException(courseName);
        List<CourseDTO> courses = teacherService.getCoursesByTeacher(teacherId);
        for (CourseDTO c : courses) {
            if (c.getName().equals(courseName)) {
                return courseRepository.getOne(courseName).getTeams()
                        .stream()
                        .map(i -> modelMapper.map(i, TeamDTO.class))
                        .collect(Collectors.toList());
            }
        }
        throw new PermissionDeniedException();
    }

    @Override
    public List<CourseDTO> getCourses() {
        return courseRepository.findAll()
                .stream()
                .map(i -> modelMapper.map(i, CourseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public boolean addCourse(CourseDTO course, String teacherId) {
        if(!teacherRepository.existsById(teacherId))
            throw new TeacherNotFoundException(teacherId);
        if(courseRepository.existsById(course.getName()))
            return false;
        Course c = modelMapper.map(course, Course.class);
        Teacher t = teacherRepository.getOne(teacherId);
        c.addTeacher(t);
        teacherRepository.save(t);
        courseRepository.save(c);
        return true;
    }

    @Override
    public boolean updateCourse(String courseName, CourseDTO courseDTO, String teacherId) {
        if(!teacherRepository.existsById(teacherId))
            throw new TeacherNotFoundException(teacherId);
        if(!courseRepository.existsById(courseName))
            throw new CourseNotFoundException(courseName);

        if(!courseName.equals(courseDTO.getName()) && courseRepository.existsById(courseDTO.getName()))
            // Corse with the new name already exist
            return false;

        List<CourseDTO> courses = teacherService.getCoursesByTeacher(teacherId);
        System.out.println(courseName);
        for (CourseDTO oldCourse : courses){
            System.out.println("LOOP");
            System.out.println(oldCourse.getName());
            if(oldCourse.getName().equals(courseName)){
                System.out.println("ENTER");
                Course c = courseRepository.getOne(courseName);
                Course newCourse = modelMapper.map(courseDTO, Course.class);
                for (Teacher t : c.getOwners()) {
                    newCourse.addTeacher(t);
                    t.getCourses().remove(c);
                    teacherRepository.save(t);
                }
                for (Student s : c.getStudents()) {
                    newCourse.addStudent(s);
                    s.getCourses().remove(c);
                    studentRepository.save(s);
                }
                for (Team t : c.getTeams()) {
                    newCourse.addTeam(t);
                    teamRepository.save(t);
                }
                for(Assignment a : c.getAssignments()){
                    a.setCourse(newCourse);
                    assignmentRepository.save(a);
                }
                courseRepository.deleteById(courseName);
                courseRepository.save(newCourse);
                return true;
            }
        }
        throw new PermissionDeniedException();
    }

    @Override
    public void deleteCourse(String courseName, String teacherId) {
        if(!teacherRepository.existsById(teacherId))
            throw new TeacherNotFoundException(teacherId);
        if(!courseRepository.existsById(courseName))
            throw new CourseNotFoundException(courseName);

        List<CourseDTO> courses = teacherService.getCoursesByTeacher(teacherId);
        for (CourseDTO courseDTO : courses){
            if(courseDTO.getName().equals(courseName)){
                Course c = courseRepository.getOne(courseName);
                for (Teacher t : c.getOwners()) {
                    t.getCourses().remove(c);
                    teacherRepository.save(t);
                }
                for (Student s : c.getStudents()) {
                    s.getCourses().remove(c);
                    studentRepository.save(s);
                }
                for (Team t : c.getTeams()) {
                    teamRepository.delete(t);
                }
                for(Assignment a : c.getAssignments()){
                    assignmentRepository.delete(a);
                }
                courseRepository.deleteById(courseName);
            }
        }
        throw new PermissionDeniedException();
    }

    @Override
    public boolean addTeacherToCourse(String courseName, String teacherId, String newTeacherId) {
        if(!teacherRepository.existsById(teacherId) || !teacherRepository.existsById(newTeacherId))
            throw new TeacherNotFoundException(teacherId);
        if(!courseRepository.existsById(courseName))
            throw new CourseNotFoundException(courseName);

        Course c = utilitsService.checkCourseOwner(courseName, teacherId);
        Teacher t = teacherRepository.getOne(newTeacherId);

        for (Teacher aux : c.getOwners())
            if(aux.getId().equals(newTeacherId))
                return false;
        c.addTeacher(t);
        courseRepository.save(c);
        teacherRepository.save(t);
        return true;
    }

    @Override
    @PreAuthorize("hasRole('TEACHER')")
    public void enableCourse(String courseName, String teacherId) {

        if(!courseRepository.existsById(courseName))
            throw new CourseNotFoundException(courseName);
        if(!teacherRepository.existsById(teacherId))
            throw new TeacherNotFoundException(teacherId);

        Course course = utilitsService.checkCourseOwner(courseName, teacherId);
        course.setStatus(true);
        courseRepository.save(course);
    }

    @Override
    @PreAuthorize("hasRole('TEACHER')")
    public void disableCourse(String courseName, String teacherId) {
        if(!courseRepository.existsById(courseName))
            throw new CourseNotFoundException(courseName);
        if(!teacherRepository.existsById(teacherId))
            throw new TeacherNotFoundException(teacherId);

        Course course = utilitsService.checkCourseOwner(courseName, teacherId);
        course.setStatus(false);
        courseRepository.save(course);
    }

    @Override
    public List<StudentDTO> getStudentsByCourse(String courseName, Principal principal) {

        Course course = utilitsService.checkCourse(courseName);

        if (principal.getName().startsWith("s")) {
            Student student = utilitsService.checkStudent(principal.getName());
            if (!student.getCourses().contains(course)) {
                throw new StudentNotEnrolledToCourseException(student.getId(), courseName);
            }
        } else {
            utilitsService.checkTeacher(principal.getName());
            utilitsService.checkCourseOwner(courseName, principal.getName());
        }

        return course.getStudents()
                .stream()
                .map(i -> modelMapper.map(i, StudentDTO.class))
                .collect(Collectors.toList());

    }

    @Override
    public boolean enrollOneStudent(String courseName, String studentId, String teacherId) {
        if(!studentRepository.existsById(studentId))
            throw new StudentNotFoundException(studentId);
        if(!courseRepository.existsById(courseName))
            throw new CourseNotFoundException(courseName);
        if(!teacherRepository.existsById(teacherId))
            throw new TeacherNotFoundException(teacherId);

        List<CourseDTO> courses = teacherService.getCoursesByTeacher(teacherId);
        for (CourseDTO c : courses) {
            if (c.getName().equals(courseName)) {
                Student student = studentRepository.getOne(studentId);
                Course course = courseRepository.getOne(courseName);
                List<Student> students = course.getStudents();
                for (Student s : students) {
                    if (s.getId().equals(studentId)) {
                        return false;
                    }
                }
                course.addStudent(student);
                return true;
            }
        }
        throw new PermissionDeniedException();
    }

    @Override
    public List<Boolean> enrollManyStudents(String courseName, List<String> studentIds, String teacherId) {
        return studentIds.stream().map(i -> enrollOneStudent(courseName, i, teacherId)).collect(Collectors.toList());
    }

    @Override
    public boolean disenrollOneStudent(String courseName, String studentId, String teacherId) {
        if(!teacherRepository.existsById(teacherId))
            throw new TeacherNotFoundException(teacherId);
        if(!courseRepository.existsById(courseName))
            throw new CourseNotFoundException(courseName);
        if(!studentRepository.existsById(studentId))
            throw new StudentNotFoundException(studentId);

        List<Student> students = courseRepository.getOne(courseName).getStudents();
        for (Student s : students)
            if(s.getId().equals(studentId)) {
                s.removeCourse(courseRepository.getOne(courseName));
                return true;
            }
        //return false if the student is not enrolled in this course
        return false;
    }

    @Override
    public List<Boolean> disenrollManyStudents(String courseName, List<String> studentIds, String teacherId) {
        return studentIds.stream().map(i -> disenrollOneStudent(courseName, i, teacherId)).collect(Collectors.toList());
    }

    @Override
    public void setVMModel(VMModelDTO vmModelDTO, String courseName, String teacherId) {
        if(!teacherRepository.existsById(teacherId))
            throw new TeacherNotFoundException(teacherId);
        if(!courseRepository.existsById(courseName))
            throw new CourseNotFoundException(courseName);

        List<CourseDTO> courses = teacherService.getCoursesByTeacher(teacherId);
        for (CourseDTO c : courses) {
            if (c.getName().equals(courseName)) {
                List<Team> teams = courseRepository.getOne(courseName).getTeams();
                int vcpuMax = 0, spaceMax = 0, ramMax = 0, activeInstancesMax = 0, instancesMax = 0;
                for (Team t : teams) {
                    int vcpuLocal = 0, spaceLocal = 0, ramLocal = 0, activeInstancesLocal = 0, instancesLocal;
                    List<VM> vms = t.getVMs();
                    instancesLocal = vms.size();
                    for (VM v : vms) {
                        vcpuLocal += v.getVcpu();
                        spaceLocal += v.getSpace();
                        ramLocal += v.getRam();
                        if (v.getState() == VmState.ON) activeInstancesLocal++;
                    }
                    vcpuMax = Math.max(vcpuLocal, vcpuMax);
                    spaceMax = Math.max(spaceLocal, spaceMax);
                    ramMax = Math.max(ramLocal, ramMax);
                    activeInstancesMax = Math.max(activeInstancesLocal, activeInstancesMax);
                    instancesMax = Math.max(instancesLocal, instancesMax);
                }
                if (vcpuMax > vmModelDTO.getVcpu() || ramMax > vmModelDTO.getRam() || spaceMax > vmModelDTO.getSpace()
                        || instancesMax > vmModelDTO.getInstances() || activeInstancesMax > vmModelDTO.getActiveInstances()){
                    throw new VMModelExcessiveLimitsException();
                }else{
                    VMModel vmModel = modelMapper.map(vmModelDTO, VMModel.class);
                    Course course = courseRepository.getOne(courseName);
                    course.setVmModel(vmModel);
                    if(vmModelRepository.existsById(vmModel.getId()))
                        vmModelRepository.deleteById(vmModel.getId());
                    courseRepository.save(course);
                    vmModelRepository.save(vmModel);
                }
            }
        }
        throw new PermissionDeniedException();
    }

    @Override
    public VMModelDTO getVMModel(String courseName, String teacherId) {
        if(!teacherRepository.existsById(teacherId))
            throw new TeacherNotFoundException(teacherId);
        if(!courseRepository.existsById(courseName))
            throw new CourseNotFoundException(courseName);

        List<CourseDTO> courses = teacherService.getCoursesByTeacher(teacherId);
        for (CourseDTO c : courses) {
            if (c.getName().equals(courseName)) {
                return modelMapper.map(courseRepository.getOne(courseName).getVmModel(), VMModelDTO.class);
            }
        }
        throw new PermissionDeniedException();
    }

    @Override
    public List<VMDTO> getVMSByCourse(String courseName, Principal principal) {
        // check if course exist
        Optional<Course> optCourse = courseRepository.findByName(courseName);
        if (optCourse.isEmpty()) {
            throw new CourseNotFoundException(courseName);
        }

        // Teacher case: get VMs of the whole course
        if (principal.getName().startsWith("d")) {
            if (!teacherRepository.existsById(principal.getName()))
                throw new TeacherNotFoundException(principal.getName());

            List<VMDTO> vms = new ArrayList<>();
            List<Course> courses = teacherRepository.getOne(principal.getName()).getCourses();

            for (Course course : courses) {
                if (course.getName().equals(courseName)) {
                    for (Team t : course.getTeams()) {
                        vms.addAll(t.getVMs().stream().map(i -> modelMapper.map(i, VMDTO.class)).collect(Collectors.toList()));
                    }
                    return vms;
                }
            }
        }

        // Student case: get VMs of the student team
        else {
            if (!studentRepository.existsById(principal.getName()))
                throw new StudentNotFoundException(principal.getName());
            // check if the student is enrolled in course
            Student student = studentRepository.getByUser_SerialNumber(principal.getName());
            Course course = optCourse.get();

            if (!student.getCourses().contains(course)) {
                throw new StudentNotEnrolledToCourseException(student.getId(), courseName);
            }

            for (Team team : student.getTeams()) {
                if (team.getCourse().equals(course)) {
                    return team.getVMs()
                            .stream()
                            .map(vm -> modelMapper.map(vm, VMDTO.class))
                            .collect(Collectors.toList());
                }
            }

            throw new StudentNotHasTeamInCourseException(student.getId(), courseName);
        }
        return null;
    }

    @Override
    public List<AssignmentDTO> getAssignmentsByCourse(Principal principal, String courseName) {

        if (!courseRepository.existsById(courseName))
            throw new CourseNotFoundException(courseName);

        if (principal.getName().startsWith("d")) {
            if (!teacherRepository.existsById(principal.getName()))
                throw new TeacherNotFoundException(principal.getName());
            List<Course> courses = teacherRepository.getOne(principal.getName()).getCourses();
            for (Course c : courses) {
                if (c.getName().equals(courseName)) {
                    return c.getAssignments()
                            .stream()
                            .map(i -> modelMapper.map(i, AssignmentDTO.class))
                            .collect(Collectors.toList());
                }
            }
            throw new PermissionDeniedException();

        } else if (principal.getName().startsWith("s")) {

            // check if student exists
            Student student = utilitsService.checkStudent(principal.getName());

            // check if student is enrolled to the course
            Course course = courseRepository.getOne(courseName);
            if( ! student.getCourses().contains(course)){
                throw new StudentNotEnrolledToCourseException(student.getId(), courseName);
            }

            List <Assignment> assignments = assignmentRepository.getAllByCourse(course);

            // for each assignment: if this is the first time that the student open the assignment a new deliveredPaper
            // will be created

            for (Assignment assignment: assignments){

                Paper paper = paperRepository.getByStudentAndAssignment(student, assignment);
                List<DeliveredPaper> deliveredPapers = deliveredPaperRepository.getAllByPaper(paper);

                boolean readPaper = false;
                for (DeliveredPaper deliveredPaper : deliveredPapers) {
                    if(deliveredPaper.getStatus().equals(PaperStatus.READ)){
                        readPaper = true;
                        break;
                    }
                }

                if(!readPaper){
                    DeliveredPaper deliveredPaper = new DeliveredPaper(PaperStatus.READ, new Date().getTime(),
                            null, paper);
                    paper.getDeliveredPapers().add(deliveredPaper);
                    paperRepository.save(paper);
                    deliveredPaperRepository.save(deliveredPaper);
                }
            }

            return assignments
                    .stream()
                    .map(assignment -> modelMapper.map(assignment, AssignmentDTO.class))
                    .collect(Collectors.toList());

        } else {
            // TODO qui non credo che ci arriviamo mai, io lo togliere
            throw new PermissionDeniedException();
        }
    }
}
