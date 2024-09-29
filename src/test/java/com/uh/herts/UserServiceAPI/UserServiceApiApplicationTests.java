package com.uh.herts.UserServiceAPI;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uh.herts.UserServiceAPI.payload.LoginDTO;
import com.uh.herts.UserServiceAPI.payload.RegisterDTO;
import com.uh.herts.UserServiceAPI.payload.RegisterResDTO;
import com.uh.herts.UserServiceAPI.payload.UserResDTO;
import com.uh.herts.UserServiceAPI.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserServiceApiApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private UserService userService;

	@Test
	void contextLoads() {
	}

	@Test
	void testCreateUserWithoutHittingDb() throws Exception {
		RegisterDTO registerDTO = new RegisterDTO();
		registerDTO.setUsername("temp");
		registerDTO.setEmail("temp.user@gmail.com");
		registerDTO.setPassword("pass123");
		registerDTO.setFirstName("John");
		registerDTO.setLastName("Doe");

		RegisterResDTO mockResponse = new RegisterResDTO(1, "temp", "John", "Doe", "temp.user@gmail.com");

		// Mock the UserService to return the mockResponse when createUser is called
		when(userService.createUser(any(RegisterDTO.class))).thenReturn(mockResponse);

		mockMvc.perform(post("/api/users/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(registerDTO)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value("temp"))
				.andExpect(jsonPath("$.firstName").value("John"))
				.andExpect(jsonPath("$.lastName").value("Doe"))
				.andExpect(jsonPath("$.email").value("temp.user@gmail.com"));
	}

	@Test
	void testGetAllUsers() throws Exception {
		mockMvc.perform(get("/api/users"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray());
	}

	@Test
	void testLoginUser() throws Exception {
		// Arrange: Create a LoginDTO with sample data
		LoginDTO loginDTO = new LoginDTO();
		loginDTO.setUsername("john");
		loginDTO.setPassword("pass123");

		// Arrange: Create a mock response UserResDTO
		UserResDTO mockUserResDTO = new UserResDTO(1, "john@example.com", "john", "John", "Doe");

		// Act: Mock the UserService's authenticateUser method to return the mockUserResDTO
		when(userService.authenticateUser(anyString(), anyString())).thenReturn(mockUserResDTO);

		// Assert: Perform a POST request to /api/users/login and verify the response
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
