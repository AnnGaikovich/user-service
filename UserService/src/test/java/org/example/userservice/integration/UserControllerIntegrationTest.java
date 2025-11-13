package org.example.userservice.integration;

import org.example.userservice.dto.UserRequestDTO;
import org.example.userservice.dto.UserResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private UserRequestDTO createUserRequestDTO() {
        UserRequestDTO userRequest = new UserRequestDTO();
        userRequest.setName("Integration");
        userRequest.setSurname("Test");
        userRequest.setEmail("integration.test@example.com");
        userRequest.setBirthDate(LocalDate.of(1990, 1, 1));
        userRequest.setActive(true);
        return userRequest;
    }

    private UserRequestDTO createUserRequestDTO(String name, String surname, String email) {
        UserRequestDTO userRequest = new UserRequestDTO();
        userRequest.setName(name);
        userRequest.setSurname(surname);
        userRequest.setEmail(email);
        userRequest.setBirthDate(LocalDate.of(1990, 1, 1));
        userRequest.setActive(true);
        return userRequest;
    }

    @Test
    void createUser_ThenGetUser_Success() {
        // Create User
        UserRequestDTO userRequest = createUserRequestDTO();

        ResponseEntity<UserResponseDTO> createResponse = restTemplate.postForEntity(
                "/api/v1/users",
                userRequest,
                UserResponseDTO.class
        );

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        assertNotNull(createResponse.getBody().getId());

        Long userId = createResponse.getBody().getId();

        // Get User
        ResponseEntity<UserResponseDTO> getResponse = restTemplate.getForEntity(
                "/api/v1/users/" + userId,
                UserResponseDTO.class
        );

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals(userId, getResponse.getBody().getId());
        assertEquals("Integration", getResponse.getBody().getName());
    }

    @Test
    void createUser_DuplicateEmail_ReturnsConflict() {
        UserRequestDTO userRequest = createUserRequestDTO("John", "Doe", "duplicate@example.com");

        // First request - should succeed
        ResponseEntity<UserResponseDTO> firstResponse = restTemplate.postForEntity(
                "/api/v1/users",
                userRequest,
                UserResponseDTO.class
        );
        assertEquals(HttpStatus.CREATED, firstResponse.getStatusCode());

        // Second request with same email - should return CONFLICT (409)
        ResponseEntity<String> secondResponse = restTemplate.postForEntity(
                "/api/v1/users",
                userRequest,
                String.class
        );
        assertEquals(HttpStatus.CONFLICT, secondResponse.getStatusCode()); // Изменили на CONFLICT
    }

    @Test
    void updateUser_Success() {
        // First create a user
        UserRequestDTO createRequest = createUserRequestDTO("Original", "Name", "original@example.com");

        ResponseEntity<UserResponseDTO> createResponse = restTemplate.postForEntity(
                "/api/v1/users",
                createRequest,
                UserResponseDTO.class
        );
        Long userId = createResponse.getBody().getId();

        // Update the user
        UserRequestDTO updateRequest = createUserRequestDTO("Updated", "Name", "updated@example.com");

        ResponseEntity<UserResponseDTO> updateResponse = restTemplate.exchange(
                "/api/v1/users/" + userId,
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest),
                UserResponseDTO.class
        );

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertEquals("Updated", updateResponse.getBody().getName());
        assertEquals("updated@example.com", updateResponse.getBody().getEmail());
    }
}