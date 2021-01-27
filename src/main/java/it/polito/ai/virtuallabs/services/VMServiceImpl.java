package it.polito.ai.virtuallabs.services;

import it.polito.ai.virtuallabs.dtos.StudentDTO;
import it.polito.ai.virtuallabs.dtos.VMDTO;
import it.polito.ai.virtuallabs.entities.Student;
import it.polito.ai.virtuallabs.entities.Team;
import it.polito.ai.virtuallabs.entities.VM;
import it.polito.ai.virtuallabs.entities.VMModel;
import it.polito.ai.virtuallabs.enums.VmState;
import it.polito.ai.virtuallabs.exceptions.studentException.StudentNotBelongToTeam;
import it.polito.ai.virtuallabs.exceptions.studentException.StudentNotHasTeamInCourseException;
import it.polito.ai.virtuallabs.exceptions.studentException.StudentNotOwnVMException;
import it.polito.ai.virtuallabs.exceptions.teacherExceptions.PermissionDeniedException;
import it.polito.ai.virtuallabs.exceptions.vmException.*;
import it.polito.ai.virtuallabs.exceptions.vmModelExceptions.VMModelNotDefinedException;
import it.polito.ai.virtuallabs.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VMServiceImpl implements VMService {

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
    UtilitsService utilitsService;

    // VM MANAGEMENT

    @Override
    public void vmChangeParam(VMDTO vmdto, Principal principal) {

        VM vm;
        if (principal.getName().startsWith("s")) {
            vm = utilitsService.checkExistingVMCondition(vmdto.getId(), principal);
        } else if (principal.getName().startsWith("d")) {
            vm = utilitsService.checkTeacherCondition(vmdto.getId(), principal);
        } else
            throw new PermissionDeniedException();

        // check if VM is off
        if(vm.getState().equals(VmState.ON)){
            throw new VmOnException();
        }

        // check if the course is active
        utilitsService.checkCourseActive(vmdto.getId());

        Team team = teamRepository.getOne(vm.getTeam().getId());
        utilitsService.updateVM(team, vmdto, vm);
    }

    @Override
    public void changeState(String vmId, String action, Principal principal) {
        if (action.matches("on")) {
            switchOnVM(vmId, principal);
        } else if (action.matches("off")) {
            switchOffVM(vmId, principal);
        } else {
            throw new NotAllowedActionException(action);
        }
    }

    @Override
    public void switchOnVM(String vmId, Principal principal) {

        VM vm;
        if (principal.getName().startsWith("s")) {
            vm = utilitsService.checkExistingVMCondition(vmId, principal);
        } else {
            vm = utilitsService.checkTeacherCondition(vmId, principal);
        }

        // check if the course is active
        utilitsService.checkCourseActive(vmId);

        // check if the number of active vms is under the edge
        int count = 0;
        for (VM auxVm : vm.getTeam().getVMs()) {
            if(auxVm.getState().equals(VmState.ON)){
                count += 1;
            }
        }

        if (count == vm.getTeam().getCourse().getVmModel().getActiveInstances()){
            throw new MaxActiveVmException(vmId);
        }

        vm.setState(VmState.ON);
        vmRepository.save(vm);
    }

    @Override
    public void switchOffVM(String vmId, Principal principal) {
        VM vm;
        if (principal.getName().startsWith("s")) {
            vm = utilitsService.checkExistingVMCondition(vmId, principal);
        } else if (principal.getName().startsWith("d")) {
            vm = utilitsService.checkTeacherCondition(vmId, principal);
        } else
            throw new PermissionDeniedException();

        // check if the course is active
        utilitsService.checkCourseActive(vmId);

        vm.setState(VmState.OFF);
        vmRepository.save(vm);
    }

    @Override
    public void deleteVM(String vmId, Principal principal) {

        VM vm;
        if (principal.getName().startsWith("s")) {
            vm = utilitsService.checkExistingVMCondition(vmId, principal);
        } else {
            vm = utilitsService.checkTeacherCondition(vmId, principal);
        }

        // check if the course is active
        utilitsService.checkCourseActive(vmId);

        // remove Vm from its team and from its owners
        Team team = vm.getTeam();
        team.getVMs().remove(vm);
        teamRepository.save(team);

        for (Student owner : vm.getOwners()) {
            owner.getVms().remove(vm);
            studentRepository.save(owner);
        }

        vmRepository.delete(vm);
    }

    @Override
    @PreAuthorize("hasRole('STUDENT')")
    public void createVM(VMDTO vmdto, String courseId,  Principal principal) {

        // check if the principal exist
        Student student = utilitsService.checkStudent(principal.getName());

        // check if the principal belongs to a team of the course
        Team studentTeam = null;
        boolean inTeam = false;
        for (Team team : student.getTeams()) {
            if(team.getCourse().getName().equals(courseId)){
                studentTeam = team;
                inTeam = true;
                break;
            }
        }

        if(!inTeam){
            throw new StudentNotHasTeamInCourseException(principal.getName(), courseId);
        }

        // check if the course is active
        if(!courseRepository.getOne(courseId).isStatus()){
            throw new VmCourseNotActive(vmdto.getId());
        }

        //check if the team is expired
        utilitsService.checkTeamExpired(studentTeam, true);

        // check if team is active
        utilitsService.checkTeamActive(studentTeam);

        // check if the value of dto are valid
        if(vmdto.getVcpu() <= 0 ){
            throw new VmParameterException("vcpu");
        }
        if (vmdto.getRam() <= 0) {
            throw new VmParameterException("ram");
        }
        if (vmdto.getSpace() <= 0) {
            throw new VmParameterException("space");
        }

        // check if the maximum number of vm for the team is already reached
        VMModel vmModel;
        int maxIstances;
        try {
            vmModel = studentTeam.getCourse().getVmModel();
            maxIstances = vmModel.getInstances();
        } catch (NullPointerException e) {
            throw new VMModelNotDefinedException(courseId);
        }

        int istances = studentTeam.getVMs().size();

        if (istances == maxIstances) {
            throw new MaxVmException();
        }

        // save new vm
        VM vm = modelMapper.map(vmdto, VM.class);
        vm.setState(VmState.OFF);
        vm.setTeam(studentTeam);
        utilitsService.updateVM(studentTeam, vmdto, vm);

        // update relationship
        studentTeam.getVMs().add(vm);
        student.getVms().add(vm);
        studentRepository.save(student);
        teamRepository.save(studentTeam);
    }

    @Override
    public byte[] execVM(String vmId, Principal principal) throws IOException {

        VM vm;
        if (principal.getName().startsWith("s")) {
            vm = utilitsService.checkExistingVMCondition(vmId, principal);
        } else {
            vm = utilitsService.checkTeacherCondition(vmId, principal);
        }
        // check if the course is active
        utilitsService.checkCourseActive(vmId);
        if (vm.getState() == VmState.OFF)
            throw new VmOffException();
        return utilitsService.fromPathToImage("vms/vm");
    }

    @Override
    @PreAuthorize("hasRole('STUDENT')")
    public List<VMDTO> getMyVms(Principal principal, String courseName) {
        Student student = utilitsService.checkStudent(principal.getName());

        List<VM> vms = student.getVms();
        List<VM> vms_toReturn = new ArrayList<>();
        for (VM vm : vms) {
            if (courseName.equals(vm.getTeam().getCourse().getName())) {
                vms_toReturn.add(vm);
            }
        }
        return vms_toReturn.stream().map(i -> modelMapper.map(i, VMDTO.class)).collect(Collectors.toList());
    }


    @Override
    @PreAuthorize("hasRole('STUDENT')")
    public List<StudentDTO> getOwners(String vmId, Principal principal) {

        Student student = utilitsService.checkStudent(principal.getName());
        if (!vmRepository.existsById(vmId)) {
            throw new VmNotFoundException(vmId);
        }
        VM vm = vmRepository.getOne(vmId);

        if (!student.getVms().contains(vm)) {
            throw new StudentNotOwnVMException(principal.getName(), vmId);
        }

        List<Student> owners = vm.getOwners();
        if (owners.contains(student)) {
            return owners.stream().map(i -> {
                String id = i.getId();
                StudentDTO studentDTO = modelMapper.map(i.getUser(), StudentDTO.class);
                studentDTO.setId(id);
                return studentDTO;
            }).collect(Collectors.toList());
        } else {
            throw new PermissionDeniedException();
        }
    }

    @Override
    public List<StudentDTO> getNotOwners(String vmId, Principal principal) {
        Student student = utilitsService.checkStudent(principal.getName());
        if (!vmRepository.existsById(vmId)) {
            throw new VmNotFoundException(vmId);
        }
        VM vm = vmRepository.getOne(vmId);

        if (!student.getVms().contains(vm)) {
            throw new StudentNotOwnVMException(principal.getName(), vmId);
        }


        List<Student> owners = vm.getOwners();
        if (owners.contains(student)) {
            List<Student> allTeam = vm.getTeam().getStudents();
            List<Student> notOwners = new ArrayList<>();

            for (Student student1 : allTeam) {
                if (!owners.contains(student1)) {
                    notOwners.add(student1);
                }
            }

            return notOwners.stream().map(i -> {
                String id = i.getId();
                StudentDTO studentDTO = modelMapper.map(i.getUser(), StudentDTO.class);
                studentDTO.setId(id);
                return studentDTO;
            }).collect(Collectors.toList());
        } else {
            throw new PermissionDeniedException();
        }
    }

    @Override
    public void addOwner(String vmId, String idStudent, Principal principal) {

        Student student = utilitsService.checkStudent(principal.getName());
        if (!vmRepository.existsById(vmId)) {
            throw new VmNotFoundException(vmId);
        }
        VM vm = vmRepository.getOne(vmId);

        if (!student.getVms().contains(vm)) {
            throw new StudentNotOwnVMException(idStudent, vmId);
        }


        List<Student> students = vm.getTeam().getStudents();
        for (Student student1 : students) {
            if (student1.getId().equals(idStudent)) {
                if (vm.getOwners().contains(student1)) {
                    throw new AlreadyHasOwnership(vmId, idStudent);
                } else {
                    vm.getOwners().add(student1);
                    student1.getVms().add(vm);

                    studentRepository.save(student1);
                    vmRepository.save(vm);
                    return;
                }
            }
        }
        throw new StudentNotBelongToTeam(idStudent, vm.getTeam().getName());

    }
}
