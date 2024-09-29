package com.uh.herts.UserServiceAPI.controller;


import com.uh.herts.UserServiceAPI.entity.User;
import com.uh.herts.UserServiceAPI.payload.LoginDTO;
import com.uh.herts.UserServiceAPI.payload.RegisterDTO;
import com.uh.herts.UserServiceAPI.payload.RegisterResDTO;
import com.uh.herts.UserServiceAPI.payload.UserResDTO;
import com.uh.herts.UserServiceAPI.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Create a new user
    @PostMapping("/register")
    public ResponseEntity<RegisterResDTO> createUser(@Valid @RequestBody RegisterDTO registerDTO) {


        RegisterResDTO createdUser = userService.createUser(registerDTO);
        return ResponseEntity.ok(createdUser);
    }

    // Get all users
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Get user by ID
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    // Login endpoint
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDTO loginRequest) {

                UserResDTO userResDTO = userService.authenticateUser(loginRequest.getUsername(), loginRequest.getPassword());
        if (userResDTO!=null) {
            return ResponseEntity.ok(userResDTO);

        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: Invalid username or password");
        }
    }

    // Update user by ID
    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable Long userId, @RequestBody User userDetails) {
        User updatedUser = userService.updateUser(userId, userDetails);
        return ResponseEntity.ok(updatedUser);
    }

    // Delete user by ID
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

}
