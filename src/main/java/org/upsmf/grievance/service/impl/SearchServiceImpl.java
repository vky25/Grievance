package org.upsmf.grievance.service.impl;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.upsmf.grievance.config.EsConfig;
import org.upsmf.grievance.dto.SearchRequest;
import org.upsmf.grievance.model.es.Ticket;
import org.upsmf.grievance.model.reponse.TicketResponse;
import org.upsmf.grievance.repository.es.TicketRepository;
import org.upsmf.grievance.service.SearchService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchServiceImpl implements SearchService {

    @Value("${es.default.page.size}")
    private int defaultPageSize;
    @Autowired
    private TicketRepository esTicketRepository;
    @Autowired
    private EsConfig esConfig;

    @Override
    public TicketResponse search(SearchRequest searchRequest) {
        //Calculate
        String keyValue = searchRequest.getSort().keySet().iterator().next();
        Pageable pageable = PageRequest.of(searchRequest.getPage(), searchRequest.getSize(), Sort.Direction.valueOf(searchRequest.getSort().get(keyValue).toUpperCase()), keyValue);
        Page<Ticket> page = esTicketRepository.findAll(pageable);
        return TicketResponse.builder().count(page.getTotalElements()).data(page.getContent()).build();
    }

    @Override
    public Map<String, Object> searchTickets(SearchRequest searchRequest) {
        //Create query for search by keyword
        SearchResponse searchResponse = null;
        String keyValue = searchRequest.getSort().keySet().iterator().next();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(createTicketSearchQuery(searchRequest))
                .from(searchRequest.getPage())
                .size(searchRequest.getSize())
                .sort(keyValue, SortOrder.valueOf(searchRequest.getSort().get(keyValue).toUpperCase()));

        org.elasticsearch.action.search.SearchRequest search = new org.elasticsearch.action.search.SearchRequest("ticket");
        search.searchType(SearchType.QUERY_THEN_FETCH);
        search.source(searchSourceBuilder);
        try {
            searchResponse = esConfig.elasticsearchClient().search(search, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Map<String, Object> response = new HashMap<>();
        List<Object> results = getDocumentsFromSearchResult(searchResponse);
        response.put("count", searchResponse.getHits().getTotalHits().value);
        response.put("results", results);
        return response;
    }

    private static List<Object> getDocumentsFromSearchResult(SearchResponse result) {
        SearchHits hits = result.getHits();
        return getDocumentsFromHits(hits);
    }

    private static List<Object> getDocumentsFromHits(SearchHits hits) {
        List<Object> documents = new ArrayList<Object>();
        for (SearchHit hit : hits) {
            documents.add(hit.getSourceAsMap());
        }
        return documents;
    }

    private BoolQueryBuilder createTicketSearchQuery(SearchRequest searchRequest) {
        BoolQueryBuilder finalQuery = QueryBuilders.boolQuery();
        // search by keyword
        if (searchRequest.getSearchKeyword() != null && !searchRequest.getSearchKeyword().isBlank()) {
            MatchQueryBuilder firstNameKeywordMatchQuery = QueryBuilders.matchQuery("requester_first_name", searchRequest.getSearchKeyword());
            MatchQueryBuilder phoneKeywordMatchQuery = QueryBuilders.matchQuery("requester_phone", searchRequest.getSearchKeyword());
            MatchQueryBuilder emailKeywordMatchQuery = QueryBuilders.matchQuery("requester_email", searchRequest.getSearchKeyword());
            BoolQueryBuilder keywordSearchQuery = QueryBuilders.boolQuery();
            keywordSearchQuery.should(firstNameKeywordMatchQuery).should(phoneKeywordMatchQuery).should(emailKeywordMatchQuery);
            finalQuery.must(keywordSearchQuery);
        }
        if (searchRequest.getDate() != null && searchRequest.getDate().getFrom() != null && searchRequest.getDate().getFrom() > 0) {
            RangeQueryBuilder fromTimestampMatchQuery = QueryBuilders.rangeQuery("created_date_ts").gte(searchRequest.getDate().getFrom());
            if (searchRequest.getDate().getTo() != null && searchRequest.getDate().getTo() > 0) {
                fromTimestampMatchQuery.lt(searchRequest.getDate().getTo());
            }
            BoolQueryBuilder timestampSearchQuery = QueryBuilders.boolQuery();
            timestampSearchQuery.must(fromTimestampMatchQuery);
            finalQuery.must(timestampSearchQuery);
        }

        if (searchRequest.getFilter().get("cc") != null) {
            MatchQueryBuilder ccMatchQuery = QueryBuilders.matchQuery("assigned_to_id", searchRequest.getFilter().get("cc"));
            BoolQueryBuilder ccSearchQuery = QueryBuilders.boolQuery();
            ccSearchQuery.must(ccMatchQuery);
            finalQuery.must(ccSearchQuery);
        }
        if (searchRequest.getFilter().get("status") != null ) {
            List list = (List) searchRequest.getFilter().get("status");
            if(list.size()>1) {
                MatchQueryBuilder statusMatchQuery = null;
                BoolQueryBuilder statusSearchQuery = QueryBuilders.boolQuery();
                for(int i=0;i<list.size();i++){
                    statusMatchQuery = QueryBuilders.matchQuery("status", list.get(i));
                    statusSearchQuery.should(statusMatchQuery);
                }
                finalQuery.must(statusSearchQuery);
            }
        }
        if (searchRequest.getIsJunk() != null) {
            MatchQueryBuilder junkMatchQuery = QueryBuilders.matchQuery("is_junk", searchRequest.getIsJunk());
            BoolQueryBuilder junkSearchQuery = QueryBuilders.boolQuery();
            junkSearchQuery.must(junkMatchQuery);
            finalQuery.must(junkSearchQuery);
        }
        if (searchRequest.getPriority() != null) {
            MatchQueryBuilder priorityMatchQuery = QueryBuilders.matchQuery("priority", searchRequest.getPriority());
            BoolQueryBuilder prioritySearchQuery = QueryBuilders.boolQuery();
            prioritySearchQuery.must(priorityMatchQuery);
            finalQuery.must(prioritySearchQuery);
        }
        if (searchRequest.getIsEscalated() != null) {
            MatchQueryBuilder esclatedMatchQuery = QueryBuilders.matchQuery("is_escalated", searchRequest.getIsEscalated());
            BoolQueryBuilder esclatedSearchQuery = QueryBuilders.boolQuery();
            esclatedSearchQuery.must(esclatedMatchQuery);
            finalQuery.must(esclatedSearchQuery);
        }
        return finalQuery;
    }
}