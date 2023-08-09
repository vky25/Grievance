package org.upsmf.grievance.service.impl;

import org.elasticsearch.index.query.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.upsmf.grievance.model.es.Feedback;
import org.upsmf.grievance.model.es.Ticket;
import org.upsmf.grievance.dto.SearchRequest;
import org.upsmf.grievance.model.reponse.TicketResponse;
import org.upsmf.grievance.repository.es.TicketRepository;
import org.upsmf.grievance.service.SearchService;

import java.util.Optional;

@Service
public class SearchServiceImpl implements SearchService {

    @Value("${es.default.page.size}")
    private int defaultPageSize;
    @Autowired
    private TicketRepository esTicketRepository;

    @Override
    public TicketResponse search(SearchRequest searchRequest) {
        Page<Ticket> page = esTicketRepository.findAll(Pageable.ofSize(defaultPageSize));
        return TicketResponse.builder().count(page.getTotalElements()).data(page.getContent()).build();
    }

}