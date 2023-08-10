package org.upsmf.grievance.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.upsmf.grievance.dto.UserDto;
import org.upsmf.grievance.model.User;

public interface IntegrationService {

    ResponseEntity<String> createUser(UserDto user) throws JsonProcessingException;

    User updateUser(String accessToken,User user) throws Exception;
}
