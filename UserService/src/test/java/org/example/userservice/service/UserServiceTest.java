package org.example.userservice.service;

import org.example.userservice.dto.UserRequestDTO;
import org.example.userservice.dto.UserResponseDTO;
import org.example.userservice.entity.User;
import org.example.userservice.exception.UserNotFoundException;
import org.example.userservice.exception.BusinessRuleException;
import org.example.userservice.mapper.UserMapper;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserRequestDTO userRequestDTO;
    private UserResponseDTO userResponseDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John");
        user.setSurname("Doe");
        user.setEmail("john.doe@example.com");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setActive(true);

        userRequestDTO = new UserRequestDTO();
        userRequestDTO.setName("John");
        userRequestDTO.setSurname("Doe");
        userRequestDTO.setEmail("john.doe@example.com");
        userRequestDTO.setBirthDate(LocalDate.of(1990, 1, 1));
        userRequestDTO.setActive(true);

        userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(1L);
        userResponseDTO.setName("John");
        userResponseDTO.setSurname("Doe");
        userResponseDTO.setEmail("john.doe@example.com");
        userResponseDTO.setBirthDate(LocalDate.of(1990, 1, 1));
        userResponseDTO.setActive(true);
        userResponseDTO.setCreatedAt(null);
        userResponseDTO.setUpdatedAt(null);
    }

    @Test
    void createUser_Success() {
        when(userRepository.findByEmail(userRequestDTO.getEmail())).thenReturn(Optional.empty());
        when(userMapper.toEntity(userRequestDTO)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponseDTO(user)).thenReturn(userResponseDTO);

        UserResponseDTO result = userService.createUser(userRequestDTO);

        assertNotNull(result);
        assertEquals(userResponseDTO.getId(), result.getId());
        verify(userRepository).findByEmail(userRequestDTO.getEmail());
        verify(userRepository).save(user);
    }

    @Test
    void createUser_EmailAlreadyExists_ThrowsException() {
        when(userRepository.findByEmail(userRequestDTO.getEmail())).thenReturn(Optional.of(user));

        assertThrows(BusinessRuleException.class, () -> userService.createUser(userRequestDTO));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toResponseDTO(user)).thenReturn(userResponseDTO);

        UserResponseDTO result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_NotFound_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void getAllUsers_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(user));

        when(userRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(userPage);
        when(userMapper.toResponseDTO(user)).thenReturn(userResponseDTO);

        Page<UserResponseDTO> result = userService.getAllUsers(pageable, null, null);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(userRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void updateUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponseDTO(user)).thenReturn(userResponseDTO);

        UserResponseDTO result = userService.updateUser(1L, userRequestDTO);

        assertNotNull(result);
        verify(userRepository).findById(1L);
        verify(userRepository).save(user);
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        assertDoesNotThrow(() -> userService.deleteUser(1L));
        verify(userRepository).delete(user);
    }

    @Test
    void activateUser_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).updateActiveStatus(1L, true);

        assertDoesNotThrow(() -> userService.activateUser(1L));
        verify(userRepository).updateActiveStatus(1L, true);
    }

    @Test
    void deactivateUser_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).updateActiveStatus(1L, false);

        assertDoesNotThrow(() -> userService.deactivateUser(1L));
        verify(userRepository).updateActiveStatus(1L, false);
    }
}