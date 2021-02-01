package it.polito.ai.virtuallabs;

import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import java.io.File;

@SpringBootApplication
@Log(topic = "Main")
public class VirtuallabsApplication extends SpringBootServletInitializer {

    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }



    public static void main(String[] args) {

        SpringApplication.run(VirtuallabsApplication.class, args);
        String root_path = "src/main/resources/static/";
        log.info("Creazione images: " + new File(root_path + "images").mkdirs());
        log.info("Creazione images: " + new File(root_path + "images/users").mkdir());
        log.info("Creazione images: " + new File(root_path + "images/assignments").mkdir());
        log.info("Creazione images: " + new File(root_path + "images/deliveredPapers").mkdir());
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(SpringBootServletInitializer.class);
    }

}
