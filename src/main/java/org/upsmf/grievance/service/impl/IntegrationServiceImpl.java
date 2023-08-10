package org.upsmf.grievance.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.upsmf.grievance.dto.UserDto;
import org.upsmf.grievance.model.User;
import org.upsmf.grievance.repository.UserRepository;
import org.upsmf.grievance.service.IntegrationService;

import java.util.Optional;

@Service
public class IntegrationServiceImpl implements IntegrationService {

    @Autowired
    private  RestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Value("${api.user.createUrl}")
    private String createUserUrl;

    @Override
    public ResponseEntity<String> createUser(UserDto user) throws JsonProcessingException {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ObjectMapper mapper=new ObjectMapper();
        String userObject=mapper.writeValueAsString(user);

        JsonNode jsonNodeObject=mapper.convertValue(user, JsonNode.class);
       JsonNode root= mapper.createObjectNode();
        ((ObjectNode) root).put("request", jsonNodeObject);
        System.out.println(root);
        ResponseEntity response= restTemplate.exchange(createUserUrl, HttpMethod.POST,
                new HttpEntity<>(userObject, headers), String.class);
        return response;
    }

    @Override
    public User updateUser(String accessToken, User user) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        Optional<User> optionalUser = userRepository.findById(user.getId());

        if (optionalUser.isPresent()) {
            User userRequest = optionalUser.get();
            userRequest.setPassword(user.getPassword());
            return userRepository.save(userRequest);
        } else {
         throw new Exception("user id is not found");
        }

    }
}
