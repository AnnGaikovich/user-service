package org.example.userservice.service;

import org.example.userservice.dto.UserRequestDTO;
import org.example.userservice.dto.UserResponseDTO;
import org.example.userservice.entity.User;
import org.example.userservice.exception.UserNotFoundException;
import org.example.userservice.exception.BusinessRuleException;
import org.example.userservice.mapper.UserMapper;
import org.example.userservice.repository.UserRepository;
import org.example.userservice.specification.UserSpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        log.info("Creating new user with email: {}", userRequestDTO.getEmail());

        if (userRepository.findByEmail(userRequestDTO.getEmail()).isPresent()) {
            throw new BusinessRuleException("User with email " + userRequestDTO.getEmail() + " already exists");
        }

        User user = userMapper.toEntity(userRequestDTO);
        User savedUser = userRepository.save(user);

        log.info("Created user with ID: {}", savedUser.getId());
        return userMapper.toResponseDTO(savedUser);
    }

    @Cacheable(value = "users", key = "#id")
    public UserResponseDTO getUserById(Long id) {
        log.info("Fetching user by ID: {} from database", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return userMapper.toResponseDTO(user);
    }

    @Cacheable(value = "users", key = "'all'")
    public Page<UserResponseDTO> getAllUsers(Pageable pageable, String firstName, String surname) {
        log.info("Fetching users with filters - firstName: {}, surname: {} from database", firstName, surname);

        Specification<User> spec = UserSpecifications.withFilters(firstName, surname);
        Page<User> usersPage = userRepository.findAll(spec, pageable);

        return usersPage.map(userMapper::toResponseDTO);
    }

    @Caching(evict = {
            @CacheEvict(value = "users", key = "#id"),
            @CacheEvict(value = "users", key = "'all'")
    })
    @Transactional
    public UserResponseDTO updateUser(Long id, UserRequestDTO userRequestDTO) {
        log.info("Updating user with ID: {}", id);

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (!existingUser.getEmail().equals(userRequestDTO.getEmail()) &&
                userRepository.countByEmailExcludingId(userRequestDTO.getEmail(), id) > 0) {
            throw new BusinessRuleException("Email " + userRequestDTO.getEmail() + " is already taken");
        }

        existingUser.setName(userRequestDTO.getName());
        existingUser.setSurname(userRequestDTO.getSurname());
        existingUser.setBirthDate(userRequestDTO.getBirthDate());
        existingUser.setEmail(userRequestDTO.getEmail());
        if (userRequestDTO.getActive() != null) {
            existingUser.setActive(userRequestDTO.getActive());
        }

        User updatedUser = userRepository.save(existingUser);
        log.info("Updated user with ID: {}", id);

        return userMapper.toResponseDTO(updatedUser);
    }

    @Caching(evict = {
            @CacheEvict(value = "users", key = "#id"),
            @CacheEvict(value = "users", key = "'all'")
    })
    @Transactional
    public void activateUser(Long id) {
        log.info("Activating user with ID: {}", id);
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.updateActiveStatus(id, true);
    }

    @Caching(evict = {
            @CacheEvict(value = "users", key = "#id"),
            @CacheEvict(value = "users", key = "'all'")
    })
    @Transactional
    public void deactivateUser(Long id) {
        log.info("Deactivating user with ID: {}", id);
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.updateActiveStatus(id, false);
    }

    @Caching(evict = {
            @CacheEvict(value = "users", key = "#id"),
            @CacheEvict(value = "users", key = "'all'")
    })
    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        userRepository.delete(user);
    }
}