package it.polito.ai.virtuallabs.controllers;

import it.polito.ai.virtuallabs.repositories.StudentRepository;
import it.polito.ai.virtuallabs.repositories.TeacherRepository;
import it.polito.ai.virtuallabs.repositories.TeamRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    ModelMapper modelMapper;

    @PostMapping(value = {"","/"})
    public void test(@RequestParam("img") MultipartFile file) throws IOException {
        String destination = "images/prova.jpg";
        File f = new File(destination);
        file.transferTo(f);
        f.
    }
}
