package it.polito.ai.virtuallabs.services;

import com.zaxxer.hikari.pool.HikariProxyCallableStatement;
import it.polito.ai.virtuallabs.dtos.StudentDTO;
import it.polito.ai.virtuallabs.dtos.VMDTO;
import it.polito.ai.virtuallabs.entities.*;
import it.polito.ai.virtuallabs.exceptions.ImageException;
import it.polito.ai.virtuallabs.exceptions.assignmentExceptions.AssignmentException;
import it.polito.ai.virtuallabs.exceptions.assignmentExceptions.AssignmentNotFoundException;
import it.polito.ai.virtuallabs.exceptions.assignmentExceptions.ExpiredAssignmentException;
import it.polito.ai.virtuallabs.exceptions.paperExceptions.PaperNotChangeableException;
import it.polito.ai.virtuallabs.exceptions.paperExceptions.PaperNotFoundException;
import it.polito.ai.virtuallabs.exceptions.studentException.StudentNotFoundException;
import it.polito.ai.virtuallabs.exceptions.studentException.StudentNotOwnVMException;
import it.polito.ai.virtuallabs.exceptions.teacherExceptions.PermissionDeniedException;
import it.polito.ai.virtuallabs.exceptions.teacherExceptions.TeacherNotFoundException;
import it.polito.ai.virtuallabs.exceptions.teamException.TeamExpiredException;
import it.polito.ai.virtuallabs.exceptions.teamException.TeamNotActivedException;
import it.polito.ai.virtuallabs.exceptions.teamException.TeamNotFoundException;
import it.polito.ai.virtuallabs.exceptions.vmException.ReachedMaximumTotalValueException;
import it.polito.ai.virtuallabs.exceptions.vmException.VmCourseNotActive;
import it.polito.ai.virtuallabs.exceptions.vmException.VmNotFoundException;
import it.polito.ai.virtuallabs.exceptions.vmException.VmParameterException;
import it.polito.ai.virtuallabs.repositories.*;
import org.apache.commons.io.FileUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UtilitsServiceImpl implements UtilitsService {

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
    TeacherRepository teacherRepository;
    @Autowired
    AssignmentRepository assignmentRepository;
    @Autowired
    PaperRepository paperRepository;

    @Override
    public Team checkTeam (String teamId){
        if(teamRepository.existsById(teamId)){
            return teamRepository.getOne(teamId);
        } else {
            throw new TeamNotFoundException(teamId);

        }
    }

    @Override
    public void checkTeamExpired(Team team, boolean activeException){
        if(!team.isActive()){
            Date date = new Date();
            Timestamp timestamp = new Timestamp(date.getTime());
            if (team.getTimeout().compareTo(timestamp) < 0) {
                removeTeam(team);
                if(activeException) {
                    throw new TeamExpiredException(team.getId());
                }
            }
        }
    }
    @Override
    public void checkTeamActive(Team team){
        if(!team.isActive()){
            throw new TeamNotActivedException(team.getId());
        }
    }
    @Override
    public void removeTeam(Team team){
        for (Student auxStudent: team.getWaitingStudents()){
            auxStudent.getProposedTeams().remove(team);
            studentRepository.save(auxStudent);
        }
        for (Student auxStudent: team.getStudents()){
            auxStudent.getTeams().remove(team);
            studentRepository.save(auxStudent);
        }

        Course course = team.getCourse();
        course.getTeams().remove(team);
        courseRepository.save(course);

        teamRepository.delete(team);
    }

    // check if the course is active
    @Override
    public void  checkCourseActive(String vmId){
        VM vm = vmRepository.getOne(vmId);
        if(!vm.getTeam().getCourse().isStatus()){
            throw new VmCourseNotActive(vmId);
        }
    }

    // check if vm exist and if the principal is its owner
    @Override
    public VM checkExistingVMCondition(String vmId, Principal principal){

        // check if Vm exist
        Optional<VM> optVm = vmRepository.findById(vmId);
        if(optVm.isEmpty()){
            throw new VmNotFoundException(vmId);
        }

        // check if the principal exists and if he is the vm's owner
        Optional<Student> optStudent = studentRepository.findByUser_SerialNumber(principal.getName());
        if(optStudent.isEmpty()){
            throw new StudentNotFoundException(principal.getName());
        }

        Student student = optStudent.get();
        VM vm = optVm.get();

        if(!vm.getOwners().contains(student)){
            throw new StudentNotOwnVMException(student.getId(), vmId);
        }

        return vm;
    }

    // check if the value of new param is valid
    @Override
    public int checkVMValue(int newValue, int oldValue, int totValue, int maxVal, String param, VM vm){

        int newValueOnDb;

        if(newValue < 0 ){
            throw  new VmParameterException(param);
        }

        if(newValue == 0 ){
            totValue += oldValue;
            newValueOnDb = oldValue;
        } else {
            totValue += newValue;
            newValueOnDb = newValue;
        }
        if (totValue > maxVal){
            throw  new ReachedMaximumTotalValueException(param);
        }

        return newValueOnDb;
    }

    // check that the new values are correct, if so, save the changes
    @Override
    public void updateVM(Team team, VMDTO vmdto, VM vm){


        // calculate the resource value for all the others vm in the same team
        int vcpuTOT = 0;
        int spaceTOT = 0;
        int ramTOT = 0;

        List<String> vmIds = new ArrayList<>();

        // TODO Attenzione le risorse cono calcolate su tutte le vm del team, non solo su quelle accese
        for (Student teamStudent : team.getStudents()) {
            for (VM studentVm : teamStudent.getVms()) {
                /* check if:
                1 ) the vm is not the one that we would change
                2 ) the vm belongs to the same team of the one that we would change
                3 ) the vm is not already in the list
                 */
                if(!vm.equals(studentVm) && team.equals(studentVm.getTeam()) && !vmIds.contains(studentVm.getId())){
                    vmIds.add(studentVm.getId());
                    vcpuTOT += studentVm.getVcpu();
                    spaceTOT += studentVm.getSpace();
                    ramTOT += studentVm.getRam();
                }
            }
        }

        // get max possible values
        VMModel vmModel = team.getCourse().getVmModel();

        // check the new value
        int aux;
        aux = checkVMValue(vmdto.getVcpu(), vm.getVcpu(), vcpuTOT, vmModel.getVcpu(), "vcpu", vm);
        vm.setVcpu(aux);
        aux = checkVMValue(vmdto.getSpace(), vm.getSpace(), spaceTOT, vmModel.getSpace(), "space", vm);
        vm.setSpace(aux);
        aux = checkVMValue(vmdto.getRam(), vm.getRam(), ramTOT, vmModel.getRam(), "ram", vm);
        vm.setRam(aux);

        vmRepository.save(vm);
    }

    @Override
    public VM checkTeacherCondition(String  vmdtoID, Principal principal) {

        if(!vmRepository.existsById(vmdtoID))
            throw new VmNotFoundException(vmdtoID);
        if(!teacherRepository.existsById(principal.getName()))
            throw new TeacherNotFoundException(principal.getName());

        List<Course> courses = teacherRepository.getOne(principal.getName()).getCourses();
        VM vm = vmRepository.getOne(vmdtoID);
        boolean found = false;
        for(Course c : courses){
            if (c.getName().equals(vm.getTeam().getCourse().getName())) {
                found = true;
                break;
            }
        }
        if(!found) {
            throw new PermissionDeniedException();
        }

        return vm;
    }
    @Override
    public Student checkStudent(String studentId){
        Optional<Student> optStudent = studentRepository.findByUser_SerialNumber(studentId);
        if(optStudent.isEmpty()){
            throw  new StudentNotFoundException(studentId);
        }

        return optStudent.get();
    }
    @Override
    public Assignment checkAssignment(String assignmentId){
        Optional<Assignment> optAssignment = assignmentRepository.findById(assignmentId);
        if(optAssignment.isEmpty()){
            throw new AssignmentNotFoundException(assignmentId);
        }
        return optAssignment.get();
    }
    @Override
    public Paper checkPaper (String paperId){
        Optional<Paper> optPaper= paperRepository.findById(paperId);
        if(optPaper.isEmpty()){
            throw  new PaperNotFoundException(paperId);
        }
        return optPaper.get();
    }

    @Override
    public Course checkCourse(String courseName) {
        Optional<Course> optCourse = courseRepository.findById(courseName);
        if(optCourse.isEmpty()){
            throw  new PaperNotFoundException(courseName);
        }
        return optCourse.get();
    }

    @Override
    public  Course checkCourseOwner (String courseName, String teacherId) {
         Course course = courseRepository.getOne(courseName);
        for (Teacher owner : course.getOwners()) {
            if(owner.getId().equals(teacherId)){
                return course;
            }
        }
        throw new PermissionDeniedException();
    }

    @Override
    public void checkExpiredAssignmentByPaper(Paper paper, boolean activeException) {

        Assignment assignment = paper.getAssignment();

        if (paper.isChangeable()) {
            Date date = new Date();
            Timestamp now = new Timestamp(date.getTime());

            if (assignment.getExpireDate().compareTo(now) < 0) {
                for (Paper assignmentPaper : assignment.getPapers()) {
                    assignmentPaper.setChangeable(false);
                    paperRepository.save(assignmentPaper);
                }
                if (activeException)
                    throw new ExpiredAssignmentException(assignment.getId());
            }
        } else {
            if (activeException)
                throw new PaperNotChangeableException(paper.getId());
        }
    }

    @Override
    public void checkExpiredAssignment(Assignment assignment, boolean activeException) {
        Date date = new Date();
        Timestamp now = new Timestamp(date.getTime());

        if (assignment.getExpireDate().compareTo(now) < 0) {
            for (Paper assignmentPaper : assignment.getPapers()) {
                assignmentPaper.setChangeable(false);
                paperRepository.save(assignmentPaper);
            }
            if (activeException)
                throw new AssignmentException(assignment.getId());
        }
    }

    @Override
    public  Teacher checkTeacher(String teachId){
        Optional<Teacher> teacher = teacherRepository.findById(teachId);
        if(teacher.isEmpty()){
            throw new TeacherNotFoundException(teachId);
        }
        return teacher.get();
    }

    @Override
    public void fromImageToPath(byte[] image, String path) {
        File file = new File("src/main/resources/static/images/" + path + ".jpg");
        try (OutputStream os = new FileOutputStream(file)) {
            os.write(image);
            os.flush();

        } catch (Exception e) {
            throw new ImageException(e.getMessage());
        }
    }

    @Override
    public byte[] fromPathToImage(String path) throws IOException {
        InputStream is = this.getClass().getResourceAsStream("/static/images/" + path + ".jpg");
        BufferedImage img = ImageIO.read(is);
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        ImageIO.write(img, "jpg", bao);
        return bao.toByteArray();
    }

    @Override
    public StudentDTO fromStudentEntityToDTO (Student student) throws IOException {
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setId(student.getId());
        studentDTO.setEmail(student.getUser().getEmail());
        studentDTO.setLastName(student.getUser().getLastName());
        studentDTO.setName(student.getUser().getName());
        studentDTO.setSerialNumber(student.getUser().getSerialNumber());
        studentDTO.setPhoto(fromPathToImage("/users/" + studentDTO.getSerialNumber()));
        return studentDTO;
    }
}
