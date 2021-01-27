package it.polito.ai.virtuallabs.task;

import it.polito.ai.virtuallabs.services.UtilitsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class RemoveCacheImgTask {

    @Autowired
    UtilitsService utilitsService;

    // remove images from cache
    @Scheduled(fixedDelay = 1200000)
    public void scheduleFixedDelayTask() {
        utilitsService.removeImgsFromMap();
    }
}