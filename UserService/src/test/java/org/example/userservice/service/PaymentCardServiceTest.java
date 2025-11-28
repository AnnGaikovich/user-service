package org.example.userservice.service;

import org.example.userservice.dto.PaymentCardRequestDTO;
import org.example.userservice.dto.PaymentCardResponseDTO;
import org.example.userservice.entity.PaymentCard;
import org.example.userservice.entity.User;
import org.example.userservice.exception.PaymentCardNotFoundException;
import org.example.userservice.exception.UserNotFoundException;
import org.example.userservice.exception.BusinessRuleException;
import org.example.userservice.mapper.PaymentCardMapper;
import org.example.userservice.repository.PaymentCardRepository;
import org.example.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentCardServiceTest {

    @Mock
    private PaymentCardRepository paymentCardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentCardMapper paymentCardMapper;

    @InjectMocks
    private PaymentCardService paymentCardService;

    private User user;
    private PaymentCard paymentCard;
    private PaymentCardRequestDTO cardRequestDTO;
    private PaymentCardResponseDTO cardResponseDTO;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);
        user.setName("John");
        user.setSurname("Doe");
        user.setEmail("john.doe@example.com");

        paymentCard = new PaymentCard();
        paymentCard.setId(1L);
        paymentCard.setNumber("4111111111111111");
        paymentCard.setHolder("JOHN DOE");
        paymentCard.setExpirationDate("12.31.2025");
        paymentCard.setActive(true);
        paymentCard.setUser(user);

        cardRequestDTO = new PaymentCardRequestDTO();
        cardRequestDTO.setNumber("4111111111111111");
        cardRequestDTO.setHolder("JOHN DOE");
        cardRequestDTO.setExpirationDate("12.31.2025");
        cardRequestDTO.setActive(true);
        cardRequestDTO.setUserId(1L);

        cardResponseDTO = new PaymentCardResponseDTO();
        cardResponseDTO.setId(1L);
        cardResponseDTO.setNumber("4111111111111111");
        cardResponseDTO.setHolder("JOHN DOE");
        cardResponseDTO.setExpirationDate("12.31.2025");
        cardResponseDTO.setActive(true);
        cardResponseDTO.setUserId(1L);
        cardResponseDTO.setUserFullName("John Doe");
        cardResponseDTO.setCreatedAt(null);
        cardResponseDTO.setUpdatedAt(null);
    }

    @Test
    void createCard_Success() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(paymentCardRepository.countByUserId(1L)).thenReturn(0L);
        when(paymentCardRepository.findByNumber("4111111111111111")).thenReturn(Optional.empty());
        when(paymentCardMapper.toEntity(cardRequestDTO)).thenReturn(paymentCard);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(paymentCardMapper.toResponseDTO(paymentCard)).thenReturn(cardResponseDTO);

        PaymentCardResponseDTO result = paymentCardService.createCard(cardRequestDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());

        verify(userRepository).save(any(User.class));

        verify(paymentCardRepository, never()).save(any(PaymentCard.class));
    }

    @Test
    void createCard_UserNotFound_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> paymentCardService.createCard(cardRequestDTO));
        verify(userRepository, never()).save(any(User.class));
        verify(paymentCardRepository, never()).save(any(PaymentCard.class));
    }

    @Test
    void createCard_MaxCardsReached_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(paymentCardRepository.countByUserId(1L)).thenReturn(5L);

        assertThrows(BusinessRuleException.class, () -> paymentCardService.createCard(cardRequestDTO));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createCard_CardNumberExists_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(paymentCardRepository.countByUserId(1L)).thenReturn(0L);
        when(paymentCardRepository.findByNumber("4111111111111111")).thenReturn(Optional.of(paymentCard));

        assertThrows(BusinessRuleException.class, () -> paymentCardService.createCard(cardRequestDTO));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getCardById_Success() {
        when(paymentCardRepository.findById(1L)).thenReturn(Optional.of(paymentCard));
        when(paymentCardMapper.toResponseDTO(paymentCard)).thenReturn(cardResponseDTO);

        PaymentCardResponseDTO result = paymentCardService.getCardById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(paymentCardRepository).findById(1L);
    }

    @Test
    void getCardById_NotFound_ThrowsException() {
        when(paymentCardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PaymentCardNotFoundException.class, () -> paymentCardService.getCardById(1L));
    }

    @Test
    void getAllCards_Success() {

        Pageable pageable = PageRequest.of(0, 10);
        Page<PaymentCard> cardPage = new PageImpl<>(List.of(paymentCard));

        when(paymentCardRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(cardPage);
        when(paymentCardMapper.toResponseDTO(paymentCard)).thenReturn(cardResponseDTO);

        Page<PaymentCardResponseDTO> result = paymentCardService.getAllCards(pageable, null, null);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(paymentCardRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getCardsByUserId_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<PaymentCard> cardPage = new PageImpl<>(List.of(paymentCard));

        when(userRepository.existsById(1L)).thenReturn(true);
        when(paymentCardRepository.findByUserId(1L, pageable)).thenReturn(cardPage);
        when(paymentCardMapper.toResponseDTO(paymentCard)).thenReturn(cardResponseDTO);

        Page<PaymentCardResponseDTO> result = paymentCardService.getCardsByUserId(1L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(paymentCardRepository).findByUserId(1L, pageable);
    }

    @Test
    void getCardsByUserId_UserNotFound_ThrowsException() {
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> paymentCardService.getCardsByUserId(1L, pageable));
    }

    @Test
    void updateCard_Success() {
        when(paymentCardRepository.findById(1L)).thenReturn(Optional.of(paymentCard));
        when(paymentCardRepository.save(paymentCard)).thenReturn(paymentCard);
        when(paymentCardMapper.toResponseDTO(paymentCard)).thenReturn(cardResponseDTO);

        PaymentCardResponseDTO result = paymentCardService.updateCard(1L, cardRequestDTO);

        assertNotNull(result);
        verify(paymentCardRepository).save(paymentCard);
        verify(paymentCardRepository, never()).findByNumber(anyString());
    }

    @Test
    void deleteCard_Success() {
        when(paymentCardRepository.findById(1L)).thenReturn(Optional.of(paymentCard));
        doNothing().when(paymentCardRepository).delete(paymentCard);

        assertDoesNotThrow(() -> paymentCardService.deleteCard(1L));
        verify(paymentCardRepository).delete(paymentCard);
    }

    @Test
    void activateCard_Success() {
        when(paymentCardRepository.findById(1L)).thenReturn(Optional.of(paymentCard));
        when(paymentCardRepository.save(paymentCard)).thenReturn(paymentCard);

        assertDoesNotThrow(() -> paymentCardService.activateCard(1L));
        assertTrue(paymentCard.getActive());
        verify(paymentCardRepository).save(paymentCard);
    }

    @Test
    void deactivateCard_Success() {
        when(paymentCardRepository.findById(1L)).thenReturn(Optional.of(paymentCard));
        when(paymentCardRepository.save(paymentCard)).thenReturn(paymentCard);

        assertDoesNotThrow(() -> paymentCardService.deactivateCard(1L));
        assertFalse(paymentCard.getActive());
        verify(paymentCardRepository).save(paymentCard);
    }
}