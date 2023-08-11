package org.upsmf.grievance.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.upsmf.grievance.dto.UserDto;
import org.upsmf.grievance.model.User;
import org.upsmf.grievance.service.IntegrationService;


@Controller
@RequestMapping("/user")
public class UserController {


    @Autowired
    private IntegrationService integrationService;


    @PostMapping("/assignRole")
    public ResponseEntity<String> assignRole(@RequestParam Long userId, @RequestParam Long roleId) {
        try {
            integrationService.assignRole(userId, roleId);
            return ResponseEntity.ok("Role assigned successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/create-user")
    public ResponseEntity<User> createUser(@RequestBody UserDto userRequest) throws Exception{
            return integrationService.createUser(userRequest);
        }
    @PutMapping("/update-user")
    public ResponseEntity<String> updateUser(@RequestBody UserDto userDto) throws Exception{
            integrationService.updateUser(userDto);
            return ResponseEntity.ok("user updated successfully");
    }

    @PostMapping("/users")
    public ResponseEntity<String> getUsers(@RequestBody JsonNode payload) throws JsonProcessingException{
        return integrationService.getUsers(payload);
    }



}
