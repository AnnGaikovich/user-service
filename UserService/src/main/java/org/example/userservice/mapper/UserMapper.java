package org.example.userservice.mapper;

import org.example.userservice.dto.UserRequestDTO;
import org.example.userservice.dto.UserResponseDTO;
import org.example.userservice.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public User toEntity(UserRequestDTO userRequestDTO) {
        if (userRequestDTO == null) {
            return null;
        }

        User user = new User();
        user.setName(userRequestDTO.getName());
        user.setSurname(userRequestDTO.getSurname());
        user.setBirthDate(userRequestDTO.getBirthDate());
        user.setEmail(userRequestDTO.getEmail());
        user.setActive(userRequestDTO.getActive());

        // Поля id, createdAt, updatedAt, paymentCards не устанавливаем -
        // они будут установлены автоматически или останутся null

        return user;
    }

    public UserResponseDTO toResponseDTO(User user) {
        if (user == null) {
            return null;
        }

        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setSurname(user.getSurname());
        dto.setBirthDate(user.getBirthDate());
        dto.setEmail(user.getEmail());
        dto.setActive(user.getActive());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());

        return dto;
    }

    public List<UserResponseDTO> toResponseDTOList(List<User> users) {
        if (users == null) {
            return null;
        }

        return users.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
}