package org.upsmf.grievance.repository.es;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import org.upsmf.grievance.model.es.Feedback;

@Repository
public interface FeedbackRepository extends ElasticsearchRepository<Feedback, String> {
}
