package org.upsmf.grievance.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.api.gax.rpc.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.upsmf.grievance.dto.UserDto;
import org.upsmf.grievance.model.User;

public interface IntegrationService {

    ResponseEntity<User> createUser(UserDto user) throws Exception;

    ResponseEntity<String> updateUser(UserDto userDto) throws Exception;

    ResponseEntity<String> getUsers(JsonNode payload) throws JsonProcessingException;
    void assignRole(Long userId, Long roleId) throws NotFoundException;
}
