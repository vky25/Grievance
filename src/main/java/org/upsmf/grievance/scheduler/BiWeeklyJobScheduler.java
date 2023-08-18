package org.upsmf.grievance.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.upsmf.grievance.constants.Constants;
import org.upsmf.grievance.dto.SearchDateRange;
import org.upsmf.grievance.dto.SearchRequest;
import org.upsmf.grievance.model.EmailDetails;
import org.upsmf.grievance.service.EmailService;
import org.upsmf.grievance.service.SearchService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@Component
public class BiWeeklyJobScheduler {

    @Autowired
    private SearchService searchService;

    @Autowired
    private EmailService emailService;

    @Value("${email.ids}")
    private List<String> emailIds;

    @Value("${subject.bi.weekly.report}")
    private String subject;

    @Scheduled(cron = "0 1 0 */14 * ?")
    public void runBiWeeklyJob(){
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setDate(SearchDateRange.builder().to(Calendar.getInstance().getTimeInMillis())
                .from(LocalDateTime.now().minusDays(14).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()).build());
        Map<String, Object> response = searchService.dashboardReport(searchRequest);

        EmailDetails emailDetails = new EmailDetails();
        for (int i=0;i<emailIds.size();i++){
            emailDetails.builder().recipient(emailIds.get(i)).msgBody((String) response.get(Constants.ASSESSMENT_MATRIX))
                    .subject(subject);
            emailService.sendSimpleMail(emailDetails);
        }
    }
}

