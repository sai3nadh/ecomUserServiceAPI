package com.uh.herts.UserServiceAPI.service;

import com.uh.herts.UserServiceAPI.entity.User;
import com.uh.herts.UserServiceAPI.payload.RegisterDTO;
import com.uh.herts.UserServiceAPI.payload.RegisterResDTO;
import com.uh.herts.UserServiceAPI.payload.UserResDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    RegisterResDTO createUser(RegisterDTO user);
    List<User> getAllUsers();
    User getUserById(Long userId);

    UserResDTO authenticateUser(String username, String password);

    User updateUser(Long userId, User userDetails);
    void deleteUser(Long userId);
}
