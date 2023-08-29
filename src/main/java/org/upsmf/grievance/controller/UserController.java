package org.upsmf.grievance.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.upsmf.grievance.dto.CreateUserDto;
import org.upsmf.grievance.dto.UserDto;
import org.upsmf.grievance.dto.UserResponseDto;
import org.upsmf.grievance.model.Department;
import org.upsmf.grievance.model.User;
import org.upsmf.grievance.service.IntegrationService;

import java.util.*;


@Controller
@RequestMapping("/api/user")
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
    public ResponseEntity createUser(@RequestBody CreateUserDto userRequest) {
        try {
            ResponseEntity<User> user =  integrationService.createUser(userRequest);
            if(user.getStatusCode() == HttpStatus.OK) {
                return createUserResponse(user.getBody());
            } else {
                return ResponseEntity.internalServerError().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    private ResponseEntity<UserResponseDto> createUserResponse(User body) {
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("Role", Arrays.asList(body.getRoles()));
        List<String> department = new ArrayList<>();
        if(body.getDepartment() != null && !body.getDepartment().isEmpty()) {
            for(Department depart : body.getDepartment()) {
                department.add(depart.getDepartmentName());
            }
        }
        attributes.put("departmentName", department);
        attributes.put("phoneNumber", Arrays.asList(body.getPhoneNumber()));
        UserResponseDto userResponseDto = UserResponseDto.builder()
                .email(body.getEmail())
                .emailVerified(body.isEmailVerified())
                .enabled(Boolean.valueOf(String.valueOf(body.getStatus())))
                .firstName(body.getFirstName())
                .lastName(body.getLastname())
                .id(body.getId())
                .keycloakId(body.getKeycloakId())
                .username(body.getUsername())
                .attributes(attributes)
                .build();
        return ResponseEntity.ok().body(userResponseDto);
    }

    @PostMapping("/user")
    public User addUser(@RequestBody User userRequest) {
        try {
            return integrationService.addUser(userRequest);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @PutMapping("/update-user")
    public ResponseEntity<String> updateUser(@RequestBody UserDto userDto) {
        try {
            integrationService.updateUser(userDto);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return ResponseEntity.ok("user updated successfully");
    }

    @GetMapping("/users")
    public ResponseEntity<String> getUsers(@RequestBody JsonNode payload) {
        try {
            return integrationService.getUsers(payload);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/info")
    public ResponseEntity<User> getUsersById(@RequestParam("id") long id) throws RuntimeException{
        return integrationService.getUserById(id);
    }

    @PostMapping("/activate")
    public ResponseEntity<User> activateUser(@RequestBody JsonNode payload) {
        try {
            return integrationService.activateUser(payload);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @PostMapping("/deactivate")
    public ResponseEntity<User> deactivateUser(@RequestBody JsonNode payload) {
        try {
            return integrationService.deactivateUser(payload);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody JsonNode body) {
        try {
            return integrationService.login(body);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
