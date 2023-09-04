package org.upsmf.grievance.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.upsmf.grievance.dto.CreateUserDto;
import org.upsmf.grievance.dto.UpdateUserDto;
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
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
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
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getLocalizedMessage());
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
        boolean enabled = false;
        if(body.getStatus() == 1) {
            enabled = true;
        }
        UserResponseDto userResponseDto = UserResponseDto.builder()
                .email(body.getEmail())
                .emailVerified(body.isEmailVerified())
                .enabled(enabled)
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
            throw new RuntimeException(e.getLocalizedMessage());
        }
    }

    @PostMapping("/update-user")
    public ResponseEntity<String> updateUser(@RequestBody UpdateUserDto userDto) {
        try {
            integrationService.updateUser(userDto);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getLocalizedMessage());
        }
        return ResponseEntity.ok("user updated successfully");
    }

    @GetMapping("/search")
    public ResponseEntity<String> search(@RequestBody JsonNode payload) {
        try {
            return integrationService.getUsers(payload);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getLocalizedMessage());
        }
    }

    @PostMapping("/users")
    public ResponseEntity<String> getUsers(@RequestBody JsonNode payload) {
        try {
            return integrationService.getUsers(payload);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getLocalizedMessage());
        }
    }

    @GetMapping("/info")
    public ResponseEntity getUsersById(@RequestParam("id") String id) throws RuntimeException{
        try {
            return integrationService.getUserById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getLocalizedMessage());
        }
    }

    @PostMapping("/activate")
    public ResponseEntity activateUser(@RequestBody JsonNode payload) {
        try {
            User user = integrationService.activateUser(payload);
            return createUserResponse(user);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error in activating user.");
        }
    }

    @PostMapping("/deactivate")
    public ResponseEntity deactivateUser(@RequestBody JsonNode payload) {
        try {
            User user = integrationService.deactivateUser(payload);
            return createUserResponse(user);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error in de-activating user.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody JsonNode body) {
        try {
            return integrationService.login(body);
        } catch (Exception e) {
            throw new RuntimeException(e.getLocalizedMessage());
        }
    }

}
