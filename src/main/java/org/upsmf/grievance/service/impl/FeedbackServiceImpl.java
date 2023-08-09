package org.upsmf.grievance.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.upsmf.grievance.dto.FeedbackDto;
import org.upsmf.grievance.model.es.Feedback;
import org.upsmf.grievance.model.reponse.FeedbackResponse;
import org.upsmf.grievance.repository.es.FeedbackRepository;
import org.upsmf.grievance.service.FeedbackService;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Value("${es.default.page.size}")
    private int defaultPageSize;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Override
    public void saveFeedback(FeedbackDto feedbackDto) {
        // TODO validate request -- all fields are mandatory except comment
        Feedback feedback = Feedback.builder()
                .firstName(feedbackDto.getFirstName())
                .lastName(feedbackDto.getLastName())
                .email(feedbackDto.getEmail())
                .phone(feedbackDto.getPhone())
                .rating(feedbackDto.getRating())
                .comment(feedbackDto.getComment()!=null?feedbackDto.getComment():"").build();
        feedbackRepository.save(feedback);
    }

    @Override
    public FeedbackResponse getFeedbacks() {
        Page<Feedback> page = feedbackRepository.findAll(Pageable.ofSize(defaultPageSize));
        return FeedbackResponse.builder().count(page.getNumberOfElements()).data(page.getContent()).build();
    }
}
