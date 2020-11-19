package it.polito.ai.virtuallabs;

import it.polito.ai.virtuallabs.entities.Team;
import it.polito.ai.virtuallabs.repositories.TeamRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class VirtuallabsApplication {

    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }



    public static void main(String[] args) {
        SpringApplication.run(VirtuallabsApplication.class, args);
    }

}
