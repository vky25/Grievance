package org.upsmf.grievance.service;

import org.springframework.stereotype.Service;
import org.upsmf.grievance.model.User;


public interface UserService {

    User registerUser(User user);
    User findByEmail(String email);

    User findByUsername(String username);

    boolean matchPassword(String rawPassword, String hashedPassword);

    void resetUserPassword(String email, String newPassword) throws Exception;

}
