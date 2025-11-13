package org.example.userservice.integration;

import org.example.userservice.dto.PaymentCardRequestDTO;
import org.example.userservice.dto.PaymentCardResponseDTO;
import org.example.userservice.dto.UserRequestDTO;
import org.example.userservice.dto.UserResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PaymentCardControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private Long userId;

    private UserRequestDTO createUserRequestDTO(String email) {
        UserRequestDTO userRequest = new UserRequestDTO();
        userRequest.setName("Card");
        userRequest.setSurname("Owner");
        userRequest.setEmail(email); // Используем переданный email
        userRequest.setBirthDate(LocalDate.of(1990, 1, 1));
        userRequest.setActive(true);
        return userRequest;
    }

    private PaymentCardRequestDTO createPaymentCardRequestDTO(Long userId) {
        PaymentCardRequestDTO cardRequest = new PaymentCardRequestDTO();
        cardRequest.setNumber("4111111111111111");
        cardRequest.setHolder("CARD HOLDER");
        // Устанавливаем строку в формате MM/YY
        cardRequest.setExpirationDate("12/25"); // Декабрь 2025
        cardRequest.setActive(true);
        cardRequest.setUserId(userId);
        return cardRequest;
    }

    @BeforeEach
    void setUp() {
        // Генерируем уникальный email для каждого теста
        String uniqueEmail = "card.owner." + System.currentTimeMillis() + "@example.com";

        UserRequestDTO userRequest = createUserRequestDTO(uniqueEmail);

        ResponseEntity<UserResponseDTO> response = restTemplate.postForEntity(
                "/api/v1/users",
                userRequest,
                UserResponseDTO.class
        );

        // Проверяем, что пользователь создан успешно
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        userId = response.getBody().getId();
        assertNotNull(userId, "User ID should not be null");
    }

    @Test
    void createCard_ThenGetCard_Success() {
        PaymentCardRequestDTO cardRequest = createPaymentCardRequestDTO(userId);

        // Create card
        ResponseEntity<PaymentCardResponseDTO> createResponse = restTemplate.postForEntity(
                "/api/v1/cards",
                cardRequest,
                PaymentCardResponseDTO.class
        );

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        assertNotNull(createResponse.getBody().getId());

        Long cardId = createResponse.getBody().getId();

        // Get card
        ResponseEntity<PaymentCardResponseDTO> getResponse = restTemplate.getForEntity(
                "/api/v1/cards/" + cardId,
                PaymentCardResponseDTO.class
        );

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals(cardId, getResponse.getBody().getId());
        assertEquals("4111111111111111", getResponse.getBody().getNumber());
    }

    @Test
    void createCard_UserNotFound_ReturnsNotFound() {
        PaymentCardRequestDTO cardRequest = new PaymentCardRequestDTO();
        cardRequest.setNumber("4111111111111111");
        cardRequest.setHolder("CARD HOLDER");
        cardRequest.setExpirationDate("12/25");
        cardRequest.setActive(true);
        cardRequest.setUserId(999L); // Non-existent user

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/cards",
                cardRequest,
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void createCard_InvalidExpirationDate_ReturnsBadRequest() {
        PaymentCardRequestDTO cardRequest = new PaymentCardRequestDTO();
        cardRequest.setNumber("4111111111111111");
        cardRequest.setHolder("CARD HOLDER");
        cardRequest.setExpirationDate("2025-12-31"); // Неправильный формат
        cardRequest.setActive(true);
        cardRequest.setUserId(userId);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/cards",
                cardRequest,
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}