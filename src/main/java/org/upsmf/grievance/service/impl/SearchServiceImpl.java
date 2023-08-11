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
    public Map<String, Object> dashboardReport(SearchRequest searchRequest) {
        //Create query for search by keyword
        SearchResponse searchJunkResponse = getDashboardSearchResponse(searchRequest, "isJunk");
        SearchResponse searchOpenStatusResponse = getDashboardSearchResponse(searchRequest, "openStatus");
        SearchResponse searchClosedStatusResponse = getDashboardSearchResponse(searchRequest, "closedStatus");
        SearchResponse searchIsEsclatedResponse = getDashboardSearchResponse(searchRequest, "isEscalated");
        Map<String, Object> response = new HashMap<>();
        response.put("isJunk", searchJunkResponse.getHits().getTotalHits().value);
        response.put("isOpen", searchOpenStatusResponse.getHits().getTotalHits().value);
        response.put("isClosed", searchClosedStatusResponse.getHits().getTotalHits().value);
        response.put("isEscalated", searchIsEsclatedResponse.getHits().getTotalHits().value);
        return response;
    }

    private SearchResponse getDashboardSearchResponse(SearchRequest searchRequest, String reportType) {
        SearchResponse searchResponse;
        BoolQueryBuilder finalQuery = QueryBuilders.boolQuery();
        finalQuery = getDateRangeQuery(searchRequest, finalQuery);
        if (reportType.equals("isJunk")) {
            finalQuery = getJunkQuery(true, finalQuery);
        } else if (reportType.equals("openStatus")) {
            List<String> list = new ArrayList<>();
            list.add("OPEN");
            finalQuery = getStatusQuery(list, finalQuery);
        } else if (reportType.equals("closedStatus")) {
            List<String> list = new ArrayList<>();
            list.add("CLOSED");
            finalQuery = getStatusQuery(list, finalQuery);
        } else if (reportType.equals("isEscalated")) {
            finalQuery = getEsclatedTicketsQuery(true, finalQuery);
        }
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(finalQuery);

        org.elasticsearch.action.search.SearchRequest search = new org.elasticsearch.action.search.SearchRequest("ticket");
        search.searchType(SearchType.QUERY_THEN_FETCH);
        search.source(searchSourceBuilder);
        try {
            searchResponse = esConfig.elasticsearchClient().search(search, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return searchResponse;
    }

    @Override
    public Map<String, Object> searchTickets(SearchRequest searchRequest) {
        //Create query for search by keyword
        SearchResponse searchResponse = null;
        searchResponse = getSearchResponse(searchRequest);
        Map<String, Object> response = new HashMap<>();
        List<Object> results = getDocumentsFromSearchResult(searchResponse);
        response.put("count", searchResponse.getHits().getTotalHits().value);
        response.put("results", results);
        return response;
    }

    private SearchResponse getSearchResponse(SearchRequest searchRequest) {
        SearchResponse searchResponse;
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
        return searchResponse;
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
        if (searchRequest.getPriority() != null) {
            MatchQueryBuilder priorityMatchQuery = QueryBuilders.matchQuery("priority", searchRequest.getPriority());
            BoolQueryBuilder prioritySearchQuery = QueryBuilders.boolQuery();
            prioritySearchQuery.must(priorityMatchQuery);
            finalQuery.must(prioritySearchQuery);
        }
        if (searchRequest.getFilter().get("cc") != null) {
            MatchQueryBuilder ccMatchQuery = QueryBuilders.matchQuery("assigned_to_id", searchRequest.getFilter().get("cc"));
            BoolQueryBuilder ccSearchQuery = QueryBuilders.boolQuery();
            ccSearchQuery.must(ccMatchQuery);
            finalQuery.must(ccSearchQuery);
        }
        getDateRangeQuery(searchRequest, finalQuery);
        getStatusQuery((List<String>) searchRequest.getFilter().get("status"), finalQuery);
        getJunkQuery(searchRequest.getIsJunk(), finalQuery);
        getEsclatedTicketsQuery(searchRequest.getIsEscalated(), finalQuery);
        return finalQuery;
    }

    private static BoolQueryBuilder getJunkQuery(Boolean isJunk, BoolQueryBuilder finalQuery) {
        if (isJunk != null) {
            MatchQueryBuilder junkMatchQuery = QueryBuilders.matchQuery("is_junk", isJunk);
            BoolQueryBuilder junkSearchQuery = QueryBuilders.boolQuery();
            junkSearchQuery.must(junkMatchQuery);
            finalQuery.must(junkSearchQuery);
        }
        return finalQuery;
    }

    private static BoolQueryBuilder getEsclatedTicketsQuery(Boolean isEscalated, BoolQueryBuilder finalQuery) {
        if (isEscalated != null) {
            MatchQueryBuilder esclatedMatchQuery = QueryBuilders.matchQuery("is_escalated", isEscalated);
            BoolQueryBuilder esclatedSearchQuery = QueryBuilders.boolQuery();
            esclatedSearchQuery.must(esclatedMatchQuery);
            finalQuery.must(esclatedSearchQuery);
        }
        return finalQuery;
    }

    private static BoolQueryBuilder getStatusQuery(List<String> statusList, BoolQueryBuilder finalQuery) {
        if (statusList != null) {
            MatchQueryBuilder statusMatchQuery = null;
            BoolQueryBuilder statusSearchQuery = QueryBuilders.boolQuery();
            for (int i = 0; i < statusList.size(); i++) {
                statusMatchQuery = QueryBuilders.matchQuery("status", statusList.get(i));
                statusSearchQuery.should(statusMatchQuery);
            }
            finalQuery.must(statusSearchQuery);

        }
        return finalQuery;
    }

    private static BoolQueryBuilder getDateRangeQuery(SearchRequest searchRequest, BoolQueryBuilder finalQuery) {
        if (searchRequest.getDate() != null && searchRequest.getDate().getFrom() != null && searchRequest.getDate().getFrom() > 0) {
            RangeQueryBuilder fromTimestampMatchQuery = QueryBuilders.rangeQuery("created_date_ts").gte(searchRequest.getDate().getFrom());
            if (searchRequest.getDate().getTo() != null && searchRequest.getDate().getTo() > 0) {
                fromTimestampMatchQuery.lt(searchRequest.getDate().getTo());
            }
            BoolQueryBuilder timestampSearchQuery = QueryBuilders.boolQuery();
            timestampSearchQuery.must(fromTimestampMatchQuery);
            finalQuery.must(timestampSearchQuery);
        }
        return finalQuery;
    }
}