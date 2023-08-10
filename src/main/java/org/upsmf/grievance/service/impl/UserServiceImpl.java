package org.upsmf.grievance.service.impl;

import com.google.api.gax.rpc.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.upsmf.grievance.model.Role;
import org.upsmf.grievance.model.User;
import org.upsmf.grievance.repository.RoleRepository;
import org.upsmf.grievance.repository.UserRepository;
import org.upsmf.grievance.service.UserService;

import java.util.Collections;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    @Override
    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Assign a default role for new users
        Role userRole = roleRepository.findByName("ROLE_USER");
      //  user.setRoles(Collections.singleton(userRole));
        return userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {

        return userRepository.findByUsername(username);
    }

    @Override
    public boolean matchPassword(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }

    @Override
    public void resetUserPassword(String email, String newPassword) throws Exception {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new Exception("User not found");
        }

        user.setPassword(newPassword);
        userRepository.save(user);
    }

    @Override
    public void assignRole(Long userId, Long roleId) throws NotFoundException {
        try {
            User user = userRepository.getById(userId);
            //Role role = roleRepository.getById(roleId);
           // user.getRoles().clear();
          //  user.getRoles().add(role);

            userRepository.save(user);
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User or Role not found", e);
        }
    }

    @Override
    public User findByEmail(String email) {

        return userRepository.findByEmail(email);
    }

}
