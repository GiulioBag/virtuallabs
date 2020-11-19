package it.polito.ai.virtuallabs.services;

import it.polito.ai.virtuallabs.dtos.*;

import javax.print.DocFlavor;
import java.io.Reader;
import java.util.List;

public interface TeacherService {

    // Teacher: course management
    // - courses

    List<CourseDTO> getCoursesByTeacher(String teacherId);

    //Teacher: Team management






}
