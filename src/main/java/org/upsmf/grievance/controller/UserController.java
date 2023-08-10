package org.upsmf.grievance.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.upsmf.grievance.dto.UserDto;
import org.upsmf.grievance.model.User;
import org.upsmf.grievance.service.IntegrationService;
import org.upsmf.grievance.service.UserService;


@Controller
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private IntegrationService integrationService;

        @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        userService.registerUser(user);
        return ResponseEntity.ok("User registered successfully.");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        User user = userService.findByEmail(email);
        // Logic for sending reset password instructions
        return ResponseEntity.ok("Password reset instructions sent.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String email, @RequestParam String newPassword) {
        try {
            userService.resetUserPassword(email, newPassword);
            return ResponseEntity.ok("Password reset successful.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("User not found.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestParam String username, @RequestParam String password) {
        User user = userService.findByUsername(username);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
        }

        // Compare the provided password with the stored hashed password
        if (userService.matchPassword(password, user.getPassword())) {
            return ResponseEntity.ok("Login successful.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
        }
    }

    @PostMapping("/assignRole")
    public ResponseEntity<String> assignRole(@RequestParam Long userId, @RequestParam Long roleId) {
        try {
            userService.assignRole(userId, roleId);
            return ResponseEntity.ok("Role assigned successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/create-user")
    public ResponseEntity<String> createUser(@RequestBody UserDto userRequest) throws JsonProcessingException {
        /*ObjectMapper newObjectMapper=new ObjectMapper();
       UserDto userDto= newObjectMapper.convertValue(userRequest,UserDto.class);*/
            integrationService.createUser(userRequest);
        return ResponseEntity.ok("User created successfully.");
        }

    public ResponseEntity<String> updateUser(@RequestHeader("x-authenticated-user-token") String accessToken, User user) throws Exception{
            integrationService.updateUser(accessToken,user);
            return ResponseEntity.ok("user updated successfully");
    }



}
