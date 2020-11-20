package it.polito.ai.virtuallabs.controllers;

import it.polito.ai.virtuallabs.dtos.UserDTO;
import it.polito.ai.virtuallabs.repositories.StudentRepository;
import it.polito.ai.virtuallabs.repositories.TeacherRepository;
import it.polito.ai.virtuallabs.repositories.TeamRepository;
import it.polito.ai.virtuallabs.services.UtilitsService;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/test")
@Log(topic = "TestController")
public class TestController {

    @Autowired
    UtilitsService utilitsService;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    ModelMapper modelMapper;

    @PostMapping(value = {"", "/"})
    public void test(@RequestBody UserDTO userDTO) throws IOException {

    log.info("eccomi" + userDTO);
    utilitsService.fromImageToPath(userDTO.getPhoto(), "prova.jpg");
    log.info("eccomi");
    }
}
