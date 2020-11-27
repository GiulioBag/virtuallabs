package it.polito.ai.virtuallabs.services;

import it.polito.ai.virtuallabs.dtos.*;
import it.polito.ai.virtuallabs.entities.*;
import it.polito.ai.virtuallabs.enums.VmState;
import it.polito.ai.virtuallabs.exceptions.courseException.CourseNotFoundException;
import it.polito.ai.virtuallabs.exceptions.studentException.StudentNotFoundException;
import it.polito.ai.virtuallabs.exceptions.teacherExceptions.PermissionDeniedException;
import it.polito.ai.virtuallabs.exceptions.teacherExceptions.TeacherNotFoundException;
import it.polito.ai.virtuallabs.exceptions.vmModelExceptions.VMModelExcessiveLimitsException;
import it.polito.ai.virtuallabs.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
public class TeacherServiceImpl implements TeacherService {

    @Autowired
    StudentRepository studentRepository;
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    TeacherRepository teacherRepository;
    @Autowired
    TeamRepository teamRepository;
    @Autowired
    AssignmentRepository assignmentRepository;
    @Autowired
    VMModelRepository vmModelRepository;
    @Autowired
    ModelMapper modelMapper;

    @Override
    public List<CourseDTO> getCoursesByTeacher(String teacherId) {
        if(!teacherRepository.existsById(teacherId))
            throw new TeacherNotFoundException(teacherId);

        return courseRepository.getCoursesByTeacher(teacherId)
                .stream()
                .map(i -> modelMapper.map(i, CourseDTO.class))
                .collect(Collectors.toList());
    }

}


