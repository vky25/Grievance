package org.upsmf.grievance.model.reponse;

import lombok.Builder;
import lombok.Data;
import org.upsmf.grievance.model.es.Feedback;

import java.util.List;

@Data
@Builder
public class FeedbackResponse {

    private int count;
    private List<Feedback> data;
}
