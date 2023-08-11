package org.upsmf.grievance.service.impl;

import org.checkerframework.checker.units.qual.C;
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
import org.upsmf.grievance.constants.Constants;
import org.upsmf.grievance.dto.SearchRequest;
import org.upsmf.grievance.enums.RequesterType;
import org.upsmf.grievance.enums.TicketPriority;
import org.upsmf.grievance.enums.TicketStatus;
import org.upsmf.grievance.model.es.Ticket;
import org.upsmf.grievance.model.reponse.TicketResponse;
import org.upsmf.grievance.repository.es.TicketRepository;
import org.upsmf.grievance.service.SearchService;

import java.io.IOException;
import java.util.*;

@Service
public class SearchServiceImpl implements SearchService {

    @Value("${es.default.page.size}")
    private int defaultPageSize;

    private static final Map<String, Object> assignmentResponse = new HashMap<>();
    private static final Map<String, Map<String, Object>> resolutionResponse = new HashMap<>();
    private static final Map<String, Object> departmentNameResponse = new HashMap<>();
    private static final Map<String, Object> performanceIndicatorsResponse = new HashMap<>();
    private static final Map<String, Object> finalResponse = new HashMap<>();
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
        Map<String, Object> response = null;
        if (searchRequest.getFilter().get("cc") != null && searchRequest.getFilter().get("cc").equals(Constants.AFFILIATION)) {
            response = getfinalResponse(searchRequest, Constants.AFFILIATION);
        } else if (searchRequest.getFilter().get("cc") != null && searchRequest.getFilter().get("cc").equals(Constants.EXAM)) {
            response = getfinalResponse(searchRequest, Constants.EXAM);
        } else if (searchRequest.getFilter().get("cc") != null && searchRequest.getFilter().get("cc").equals(Constants.ADMISSION)) {
            response = getfinalResponse(searchRequest, Constants.ADMISSION);
        } else if (searchRequest.getFilter().get("cc") != null && searchRequest.getFilter().get("cc").equals(Constants.REGISTRATION)) {
            response = getfinalResponse(searchRequest, Constants.REGISTRATION);
        } else if (searchRequest.getFilter().get("cc") != null && searchRequest.getFilter().get("cc").equals(Constants.ASSESSMENT)) {
            response = getfinalResponse(searchRequest, Constants.ASSESSMENT);
        } else {
            response = getfinalResponse(searchRequest, Constants.AFFILIATION);
        }
        return response;
    }

    private Map<String, Object> getfinalResponse(SearchRequest searchRequest, Long cc) {
        SearchResponse searchJunkResponse = getDashboardSearchResponse(searchRequest, "isJunk", cc);
        SearchResponse searchOpenStatusResponse = getDashboardSearchResponse(searchRequest, "openStatus", cc);
        SearchResponse searchClosedStatusResponse = getDashboardSearchResponse(searchRequest, "closedStatus", cc);
        SearchResponse searchIsEsclatedResponse = getDashboardSearchResponse(searchRequest, "isEscalated", cc);
        SearchResponse searchUnassignedResponse = getDashboardSearchResponse(searchRequest, "openStatus", cc);
        SearchResponse searchOpenTicketsResponse = getDashboardSearchResponse(searchRequest, "openStatus", cc);

        Map<String, Object> response = new HashMap<>();
        //response.put(Constants.TOTAL_TICKETS, esTicketRepository.findAllById());
        response.put(Constants.IS_JUNK, searchJunkResponse.getHits().getTotalHits().value);
        response.put(Constants.IS_OPEN, searchOpenStatusResponse.getHits().getTotalHits().value);
        response.put(Constants.IS_CLOSED, searchClosedStatusResponse.getHits().getTotalHits().value);
        response.put(Constants.IS_ESCALATED, searchIsEsclatedResponse.getHits().getTotalHits().value);
        assignmentResponse.put(Constants.ASSESSMENT_MATRIX,response);
        if(cc.equals(Constants.ASSESSMENT)){
            departmentNameResponse.put(Constants.ASSESSMENT_DEPARTMENT,response);
        } else if (cc.equals(Constants.AFFILIATION)) {
            departmentNameResponse.put(Constants.AFFILIATION_NAME,response);
        } else if (cc.equals(Constants.EXAM)) {
            departmentNameResponse.put(Constants.EXAM_NAME,response);
        } else if (cc.equals(Constants.REGISTRATION)) {
            departmentNameResponse.put(Constants.REGISTRATION_NAME,response);
        } else if (cc.equals(Constants.ADMISSION)) {
            departmentNameResponse.put(Constants.ADMISSION_NAME,response);
        }
        resolutionResponse.put(Constants.RESOLUTION_MATRIX,departmentNameResponse);
        //finalResponse.put()
        return response;
    }

    private SearchResponse getDashboardSearchResponse(SearchRequest searchRequest, String reportType, Long cc) {
        SearchResponse searchResponse;
        BoolQueryBuilder finalQuery = QueryBuilders.boolQuery();
        finalQuery = getDateRangeQuery(searchRequest, finalQuery);
        finalQuery = getCCRangeQuery(cc, finalQuery);
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
            Ticket esTicket = new Ticket();
            for (Map.Entry entry : hit.getSourceAsMap().entrySet()) {
                String key = (String) entry.getKey();
                switch (key) {
                    case "ticket_id":
                        Long longValue = ((Number) entry.getValue()).longValue();
                        esTicket.setTicketId(longValue);
                        break;
                    case "requester_first_name":
                        esTicket.setFirstName((String) entry.getValue());
                        break;
                    case "requester_last_name":
                        esTicket.setLastName((String) entry.getValue());
                        break;
                    case "requester_phone":
                        esTicket.setPhone((String) entry.getValue());
                        break;
                    case "requester_email":
                        esTicket.setEmail((String) entry.getValue());
                        break;
                    case "requester_type":
                        for (RequesterType enumValue : RequesterType.values()) {
                            if (enumValue.name().equals(entry.getValue().toString())) {
                                esTicket.setRequesterType(enumValue);
                                break;
                            }
                        }
                        break;
                    case "assigned_to_id":
                        longValue = ((Number) entry.getValue()).longValue();
                        esTicket.setAssignedToId(longValue);
                        break;
                    case "assigned_to_name":
                        esTicket.setAssignedToName((String) entry.getValue());
                        break;
                    case "description":
                        esTicket.setDescription((String) entry.getValue());
                        break;
                    case "is_junk":
                        esTicket.setJunk((Boolean) entry.getValue());
                        break;
                    case "created_date":
                        esTicket.setCreatedDate((String) entry.getValue());
                        break;
                    case "updated_date":
                        esTicket.setUpdatedDate((String) entry.getValue());
                        break;
                    case "created_date_ts":
                        longValue = ((Number) entry.getValue()).longValue();
                        esTicket.setCreatedDateTS(longValue);
                        break;
                    case "updated_date_ts":
                        longValue = ((Number) entry.getValue()).longValue();
                        esTicket.setUpdatedDateTS(longValue);
                        break;
                    case "last_updated_by":
                        longValue = ((Number) entry.getValue()).longValue();
                        esTicket.setLastUpdatedBy(longValue);
                        break;
                    case "is_escalated":
                        esTicket.setEscalated((Boolean) entry.getValue());
                        break;
                    case "escalated_date":
                        esTicket.setEscalatedDate((String) entry.getValue());
                        break;
                    case "escalated_date_ts":
                        longValue = ((Number) entry.getValue()).longValue();
                        esTicket.setEscalatedDateTS(longValue);
                        break;
                    case "escalated_to":
                        longValue = ((Number) entry.getValue()).longValue();
                        esTicket.setEscalatedTo(longValue);
                        break;
                    case "status":
                        for (TicketStatus enumValue : TicketStatus.values()) {
                            if (enumValue.name().equals(entry.getValue().toString())) {
                                esTicket.setStatus(enumValue);
                                break;
                            }
                        }
                        break;
                    case "request_type":
                        esTicket.setRequestType((String) entry.getValue());
                        break;
                    case "priority":
                        for (TicketPriority enumValue : TicketPriority.values()) {
                            if (enumValue.name().equals(entry.getValue().toString())) {
                                esTicket.setPriority(enumValue);
                                break;
                            }
                        }
                        break;
                    case "escalated_by":
                        longValue = ((Number) entry.getValue()).longValue();
                        esTicket.setEscalatedBy(longValue);
                        break;
                }
            }
            documents.add(esTicket);
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
        getCCRangeQuery((Long) searchRequest.getFilter().get("cc"), finalQuery);
        getDateRangeQuery(searchRequest, finalQuery);
        getStatusQuery((List<String>) searchRequest.getFilter().get("status"), finalQuery);
        getJunkQuery(searchRequest.getIsJunk(), finalQuery);
        getEsclatedTicketsQuery(searchRequest.getIsEscalated(), finalQuery);
        return finalQuery;
    }

    private static BoolQueryBuilder getCCRangeQuery(Long cc, BoolQueryBuilder finalQuery) {
        if (cc != null) {
            MatchQueryBuilder ccMatchQuery = QueryBuilders.matchQuery("assigned_to_id", cc);
            BoolQueryBuilder ccSearchQuery = QueryBuilders.boolQuery();
            ccSearchQuery.must(ccMatchQuery);
            finalQuery.must(ccSearchQuery);
        }
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