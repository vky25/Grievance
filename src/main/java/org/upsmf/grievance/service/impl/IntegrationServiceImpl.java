package org.upsmf.grievance.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.api.gax.rpc.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.upsmf.grievance.dto.CreateUserDto;
import org.upsmf.grievance.dto.UserCredentials;
import org.upsmf.grievance.dto.UserDto;
import org.upsmf.grievance.exception.runtime.InvalidRequestException;
import org.upsmf.grievance.model.User;
import org.upsmf.grievance.repository.UserRepository;
import org.upsmf.grievance.service.IntegrationService;

@Service
@Slf4j
public class IntegrationServiceImpl implements IntegrationService {

    @Autowired
    private  RestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Value("${api.user.createUrl}")
    private String createUserUrl;

    @Value("${api.user.updateUrl}")
    private String updateUserUrl;
    @Value("${api.user.searchUrl}")
    private String apiUrl;
    @Value("${api.user.activeUserUrl}")
    private String activeUserUrl;

    @Value("${api.user.deactivateUserUrl}")
    private String deactivateUserUrl;
    @Value("${api.user.loginUserUrl}")
    private String loginUserUrl;

    @Override
    public User addUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public ResponseEntity<User> createUser(CreateUserDto user) throws Exception {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNodeObject = mapper.convertValue(user, JsonNode.class);
        JsonNode root = mapper.createObjectNode();
        ((ObjectNode) root).put("request", jsonNodeObject);
        log.info("Create user Request - {}", root);
        ResponseEntity response = restTemplate.exchange(createUserUrl, HttpMethod.POST,
                new HttpEntity<>(root, headers), String.class);
        log.info("Create user Response - {}", response);
        if (response.getStatusCode() == HttpStatus.OK) {

            JsonNode apiResponse = mapper.readTree(response.getBody().toString());

            String userContent = apiResponse.path("result").path("userId").asText();
            ObjectNode filtersNode = mapper.createObjectNode();
            filtersNode.put("userId", userContent);

            ObjectNode requestNode = mapper.createObjectNode();
            requestNode.set("filters", filtersNode);
            requestNode.put("offset", 0);
            requestNode.put("limit", 10);
            requestNode.set("fields", mapper.createArrayNode()); // Empty fields array
            JsonNode payload = requestNode;
            JsonNode payloadRoot = mapper.createObjectNode();
            ((ObjectNode) payloadRoot).put("request", payload);
            ResponseEntity<String> getUsersResponse = getUsers(payloadRoot);

            if (getUsersResponse.getStatusCode() == HttpStatus.OK) {
                String getUsersResponseBody = getUsersResponse.getBody();
                JsonNode getUsersJsonNode = mapper.readTree(getUsersResponseBody);

                JsonNode userContentData = getUsersJsonNode.get(0);

                User newUser = createUserWithApiResponse(userContentData);
                User savedUser = userRepository.save(newUser);

                return new ResponseEntity<>(savedUser, HttpStatus.OK);
            }
            else {
                // Handle error cases here
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else{
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    public ResponseEntity<String> updateUser(UserDto userDto) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.createObjectNode();
        JsonNode request= mapper.createObjectNode();
        //((ObjectNode) request).put("userId",userDto.getUserId());
        ((ObjectNode) request).put("password",userDto.getPassword());
        ((ObjectNode) root).put("request", request);
        //ToDo need to create dynamicaly create body
            ResponseEntity<String> response = restTemplate.exchange(
                    updateUserUrl, HttpMethod.PUT,
                    new HttpEntity<>(root, headers), String.class
            );
            return response;

    }

    @Override
    public void assignRole(Long userId, Long roleId) throws NotFoundException {
        try {
            User user = userRepository.getById(userId);

            userRepository.save(user);
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User or Role not found", e);
        }
    }


    private User createUserWithApiResponse(JsonNode userContent)throws Exception{
        String[] rolesArray = new String[0];

        JsonNode rolesNode = userContent.path("roles");
        if (rolesNode.isArray()) {
            rolesArray = new String[rolesNode.size()];
            for (int i = 0; i < rolesNode.size(); i++) {
                rolesArray[i] = rolesNode.get(i).asText();
            }
        }
        return User.builder()
                .keycloakId(userContent.path("userId").asText())
                .firstName(userContent.path("firstName").asText())
                .lastname(userContent.path("lastname").asText())
                .username(userContent.path("userName").asText())
                .phoneNumber(userContent.path("phoneNumber").asText())
                .email(userContent.path("email").asText())
                .emailVerified(userContent.path("emailVerified").asBoolean())
                .status(userContent.path("status").asInt())
                .roles(rolesArray)
                .build();

    }

    @Override
    public ResponseEntity<String> getUsers(JsonNode payload) throws JsonProcessingException {

        ResponseEntity response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                new HttpEntity<>(payload),
                String.class
        );

        return response;

    }

    @Override
    public ResponseEntity<User> activateUser(JsonNode payload) throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        ResponseEntity<String> response = restTemplate.exchange(
                activeUserUrl, HttpMethod.POST,
                new HttpEntity<>(payload), String.class
        );
        if (response.getStatusCode() == HttpStatus.OK) {
            String getUsersResponseBody = response.getBody();
            JsonNode getUsersJsonNode = mapper.readTree(getUsersResponseBody);

            JsonNode userContentData = getUsersJsonNode.path("result").path("response").path("content").get(0);

            User newUser = createUserWithApiResponse(userContentData);
            User savedUser = userRepository.save(newUser);

            return new ResponseEntity<>(savedUser, HttpStatus.OK);
        }
        else {
            // Handle error cases here
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<User> deactivateUser(JsonNode payload) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ResponseEntity<String> response = restTemplate.exchange(
                deactivateUserUrl, HttpMethod.POST,
                new HttpEntity<>(payload), String.class
        );
        if (response.getStatusCode() == HttpStatus.OK) {
            String getUsersResponseBody = response.getBody();
            JsonNode getUsersJsonNode = mapper.readTree(getUsersResponseBody);

            JsonNode userContentData = getUsersJsonNode.path("result").path("response").path("content").get(0);

            User newUser = createUserWithApiResponse(userContentData);
            User savedUser = userRepository.save(newUser);

            return new ResponseEntity<>(savedUser, HttpStatus.OK);
        }
        else {
            // Handle error cases here
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<String> login(JsonNode body) {
        ResponseEntity<String> response = restTemplate.exchange(
                loginUserUrl, HttpMethod.POST,
                new HttpEntity<>(body), String.class
        );
        return response;
    }


    /**
     *  API to change password
     *  sample body -
     *  {
     *     "credentials": [
     *       {
     *         "type": "password",
     *         "value": "ka09eF$299",
     *         "temporary": "false"
     *       }
     *     ]
     *     }
     * }
     * @param userCredentials
     */
    public void changePassword(UserCredentials userCredentials) {
        // validate Request
        validateChangePasswordRequest(userCredentials);
        ObjectMapper mapper = new ObjectMapper();

    }

    private void validateChangePasswordRequest(UserCredentials userCredentials) {
        if(userCredentials == null) {
            throw new InvalidRequestException("Invalid Request");
        }

    }


}
