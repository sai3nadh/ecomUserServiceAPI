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
//        User user= new User(registerDTO.getUsername(), registerDTO.getEmail(), registerDTO.getPassword(),
//                registerDTO.getFirstName(), registerDTO.getLastName()
//        );
//        User returnUser =  userRepository.save(user);
//
//
//        return new RegisterResDTO(returnUser.getUsername(),returnUser.getFirstName(),returnUser.getLastName(), registerDTO.getEmail() );

        try {
            User user = new User(registerDTO.getUsername(), registerDTO.getEmail(), registerDTO.getPassword(),
                    registerDTO.getFirstName(), registerDTO.getLastName());
            User returnUser = userRepository.save(user);

            return new RegisterResDTO(returnUser.getUserId(), returnUser.getUsername(), returnUser.getFirstName(), returnUser.getLastName(), registerDTO.getEmail());

        } catch (DataIntegrityViolationException ex) {
//            System.out.println("entered to error");
            // Check if the exception is caused by a duplicate entry for the email
//            if (ex.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
//                if (ex.getCause().getCause().getMessage().contains("Duplicate entry") && ex.getCause().getCause().getMessage().contains("for key 'users.email'")) {
//                    // Handle the duplicate email error
////                    throw new CustomException("Email already exists", HttpStatus.CONFLICT); // Assuming you have a CustomException class
//                    throw new UserAPIException( HttpStatus.CONFLICT,"Email already exists"); // Assuming you have a CustomException class
//                }
//            }

            Throwable rootCause = ex.getRootCause();
//            if (rootCause instanceof ConstraintViolationException) {
//                ConstraintViolationException constraintEx = (ConstraintViolationException) rootCause;
//
//                // Check if it's a duplicate entry error
//                if (constraintEx.getSQLException().getSQLState().equals("23000")) {
//                    throw new UserAPIException( HttpStatus.CONFLICT,"Email already exists"); // Assuming you have a CustomException class
//                }
//            }
//
//
////            Throwable rootCause = ex.getRootCause();
//            System.out.println("Root cause: " + rootCause);
//
//            // Check if the root cause is a ConstraintViolationException
//            if (rootCause instanceof ConstraintViolationException) {
//                ConstraintViolationException constraintEx = (ConstraintViolationException) rootCause;
//
//                // Check if it's a duplicate entry error
//                if ("23000".equals(constraintEx.getSQLException().getSQLState())) {
//                    System.out.println("Duplicate email detected");
//                    throw new UserAPIException(HttpStatus.CONFLICT, "Email already exists");
//                }
//            }
//            // Re-throw the exception if it's not the specific error you're checking for
//            System.out.println(" thrown hereeex");


            // Check if the root cause is a SQLIntegrityConstraintViolationException
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
                return new UserResDTO(user.getUserId(), user.getUsername(),user.getFirstName(),user.getLastName(),user.getEmail());
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
//            user.setRole(userDetails.getRole());
            return userRepository.save(user);
        }
        return null;
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }


    /*
    @Autowired
    private UserRepository userRepository;

//    @Autowired
//    private ModelMapper mapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository//,ModelMapper mapper
    ) {
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public List<UserDto> getAllUsers() {

        List<User> users = userRepository.findAll();
        return  users.stream().map(user -> mapToDTO(user)).collect(Collectors.toList());

    }

    @Transactional
    @Override
    public ResponseEntity<UserDto> createUser(UserDto userDto) {

        User user = mapToEntity(userDto);
        User user1= userRepository.save(user);
        return  ResponseEntity.status(HttpStatus.CREATED).body(mapToDTO(user1));
    }

    @Transactional
    @Override
    public ResponseEntity<UserDto> getUserById(String userId) {
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("User","id", String.valueOf(userId)));
        return ResponseEntity.status(HttpStatus.OK).body(mapToDTO(user));
      }

      @Transactional
    @Override
    public List<User> saveUsers(List<User> users) {
        List<User> savedUsers= userRepository.saveAll(users);
        return savedUsers;
    }

    @Transactional
    @Override
    public void deleteUser(String userId) {
        userRepository.deleteByUserId(userId);
    }

    @Transactional
    @Override
    public UserDto updateUser(UserRequestDto userRequestDto) {
        User user = mapReqDtoToEntity(userRequestDto);

        User updatedUser1 =null;
        User updatedUser = userRepository.findByUserId(user.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User","id", String.valueOf(userRequestDto.getUserId())));;
//        LocalDateTime currentDateTime = LocalDateTime.now();
        if (user != null) {
            if (user.getFirstname() != null) {
                updatedUser.setFirstname(user.getFirstname());
            }
            if (user.getLastname() != null) {
                updatedUser.setLastname(user.getLastname());
            }
            if (user.getEmail() != null) {
                updatedUser.setEmail(user.getEmail());
            }
            if (user.getPhotoId() != null) {
                updatedUser.setPhotoId(user.getPhotoId());
            }
            if (user.getContact() != -1) {
                updatedUser.setContact(user.getContact());
            }

            if (user.getContactCode() != null) {
                updatedUser.setContactCode(user.getContactCode());
            }
            if (user.getGender() != null) {
                updatedUser.setGender(user.getGender());
            }

//            updatedUser.setUpdatedDate(currentDateTime);

            updatedUser1= userRepository.save(updatedUser);
        }
        return mapToDTO(updatedUser1);
    }

    @Transactional
    @Override
    public User disableUser(String userId) {
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("User","id", String.valueOf(userId)));
        User updatedUser = null;
//        LocalDateTime currentDateTime = LocalDateTime.now();
        if (user != null) {
//            user.setUpdatedDate(currentDateTime);
            updatedUser= userRepository.save(user);
        }
        return updatedUser;
    }

    @Transactional
    @Override
    public List<UserDto> getEmailsByUsername(List<String> userIds) {
//        List<User> users = userRepository.findByUsernameIn(userIds);
        List<User> users = userRepository.findByUserIdIn(userIds);
       *//* List<String> emails= users.stream()
                .map(User::getEmail)
                .collect(Collectors.toList());*//*

        List<UserDto> userEmails = users.stream()
                .map(userEmail -> new UserDto(userEmail.getUserId(),userEmail.getEmail(),
                        userEmail.getFirstname(),userEmail.getLastname()))
                .collect(Collectors.toList());
        return userEmails;
    }


    private UserDto mapToDTO(User user){
        UserDto userDto =mapper.map(user,UserDto.class);
        return userDto;
    }

    //convert DTO to entity
    private User mapToEntity(UserDto userDto){

        User user = mapper.map(userDto,User.class);
        return user;
    }

    private User mapReqDtoToEntity(UserRequestDto userRequestDto){

        User user = mapper.map(userRequestDto,User.class);
        return user;
    }*/
}
