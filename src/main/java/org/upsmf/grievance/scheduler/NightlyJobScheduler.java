package org.upsmf.grievance.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NightlyJobScheduler {

    @Scheduled(cron = "0 0 0 * * ?")
    public void runNightlyJob(){

    }
}
