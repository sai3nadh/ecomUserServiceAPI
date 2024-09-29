package com.uh.herts.UserServiceAPI;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uh.herts.UserServiceAPI.entity.User;
import com.uh.herts.UserServiceAPI.payload.LoginDTO;
import com.uh.herts.UserServiceAPI.payload.RegisterDTO;
import com.uh.herts.UserServiceAPI.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")  // Use the test profile to ensure an in-memory database is used
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserServiceApiIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testCreateUserIntegration() throws Exception {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername("temp");
        registerDTO.setEmail("temp.user@gmail.com");
        registerDTO.setPassword("pass123");
        registerDTO.setFirstName("John");
        registerDTO.setLastName("Doe");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("temp"));

        // Verify the user is correctly saved in the in-memory database
        User savedUser = userRepository.findByUsername("temp");
        assertNotNull(savedUser);
        assertEquals("temp.user@gmail.com", savedUser.getEmail());
    }

    @Test
    void testLoginUserIntegration() throws Exception {
        // Save a user directly in the repository for the test
        User user = new User("john", "john@example.com", "pass123", "John", "Doe");
        userRepository.save(user);

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("john");
        loginDTO.setPassword("pass123");

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }
}
