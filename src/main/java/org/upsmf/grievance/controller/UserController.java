package org.upsmf.grievance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.upsmf.grievance.model.User;
import org.upsmf.grievance.service.UserService;


@Controller
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

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




}
