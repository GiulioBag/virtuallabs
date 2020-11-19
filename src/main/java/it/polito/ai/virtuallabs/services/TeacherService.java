package it.polito.ai.virtuallabs.services;

import it.polito.ai.virtuallabs.dtos.*;

import javax.print.DocFlavor;
import java.io.Reader;
import java.util.List;

public interface TeacherService {

    List<CourseDTO> getCoursesByTeacher(String teacherId);

}
