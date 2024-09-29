package com.uh.herts.UserServiceAPI.service.impl;

import com.uh.herts.UserServiceAPI.entity.User;
import com.uh.herts.UserServiceAPI.exception.UserAPIException;
import com.uh.herts.UserServiceAPI.payload.RegisterDTO;
import com.uh.herts.UserServiceAPI.payload.RegisterResDTO;
import com.uh.herts.UserServiceAPI.payload.UserResDTO;
import com.uh.herts.UserServiceAPI.repository.UserRepository;
import com.uh.herts.UserServiceAPI.service.UserService;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public RegisterResDTO createUser(RegisterDTO registerDTO) {

        try {
            User user = new User(registerDTO.getUsername(), registerDTO.getEmail(), registerDTO.getPassword(),
                    registerDTO.getFirstName(), registerDTO.getLastName());
            User returnUser = userRepository.save(user);

            return new RegisterResDTO(returnUser.getUserId(), returnUser.getUsername(), returnUser.getFirstName(), returnUser.getLastName(), registerDTO.getEmail());

        } catch (DataIntegrityViolationException ex) {

            Throwable rootCause = ex.getRootCause();

            if (rootCause instanceof SQLIntegrityConstraintViolationException) {
                SQLIntegrityConstraintViolationException sqlEx = (SQLIntegrityConstraintViolationException) rootCause;

                // Check if it's a duplicate entry error
                if (sqlEx.getSQLState().equals("23000")) {
                    System.out.println("Duplicate email detected");
                    throw new UserAPIException(HttpStatus.CONFLICT, "Email already exists");
                }
            }
            throw ex;
        }
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.orElse(null);
    }

    @Override
    public UserResDTO authenticateUser(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            if( password.equalsIgnoreCase(user.getPasswordHash())){
                return new UserResDTO(user.getUserId(), user.getEmail(),user.getUsername(),user.getFirstName(),user.getLastName());
            };
        }
        return null;
    }

    @Override
    public User updateUser(Long userId, User userDetails) {
        User user = getUserById(userId);
        if (user != null) {
            user.setUsername(userDetails.getUsername());
            user.setPasswordHash(userDetails.getPasswordHash());
            user.setEmail(userDetails.getEmail());

            return userRepository.save(user);
        }
        return null;
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

}
