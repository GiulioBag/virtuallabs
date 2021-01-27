package it.polito.ai.virtuallabs.task;

import it.polito.ai.virtuallabs.entities.JwtBlacklist;
import it.polito.ai.virtuallabs.repositories.JwtBlacklistRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Base64;
import java.util.Calendar;
import java.util.List;

@Component
@Log(topic = "JwtDeleteTask")
public class JwtDeleteTask {

    @Autowired
    JwtBlacklistRepository jwtBlacklistRepository;

    @Value("${security.jwt.token.secret-key:secret}")
    private String secretKey = "secret";

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }


    // remove expired token from repositoty
    @Scheduled(fixedDelay = 1200000)
    public void scheduleFixedDelayTask() {

        log.info("JwtDeleteTask starts!");
        List<JwtBlacklist> jwtBlacklists = jwtBlacklistRepository.findAll();
        for (JwtBlacklist jwtBlacklist : jwtBlacklists) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR, -1);
            if(calendar.getTime().after(jwtBlacklist.getDate())) {
                jwtBlacklistRepository.delete(jwtBlacklist);
            }
        }
    }
}
