package org.example.userservice.mapper;

import org.example.userservice.dto.PaymentCardRequestDTO;
import org.example.userservice.dto.PaymentCardResponseDTO;
import org.example.userservice.entity.PaymentCard;
import org.example.userservice.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PaymentCardMapper {

    public PaymentCard toEntity(PaymentCardRequestDTO paymentCardRequestDTO) {
        if (paymentCardRequestDTO == null) {
            return null;
        }

        PaymentCard paymentCard = new PaymentCard();
        paymentCard.setNumber(paymentCardRequestDTO.getNumber());
        paymentCard.setHolder(paymentCardRequestDTO.getHolder());
        paymentCard.setExpirationDate(paymentCardRequestDTO.getExpirationDate());
        paymentCard.setActive(paymentCardRequestDTO.getActive());

        // Обработка userIdToUser
        if (paymentCardRequestDTO.getUserId() != null) {
            User user = new User();
            user.setId(paymentCardRequestDTO.getUserId());
            paymentCard.setUser(user);
        }

        return paymentCard;
    }

    public PaymentCardResponseDTO toResponseDTO(PaymentCard paymentCard) {
        if (paymentCard == null) {
            return null;
        }

        PaymentCardResponseDTO dto = new PaymentCardResponseDTO();
        dto.setId(paymentCard.getId());
        dto.setNumber(paymentCard.getNumber());
        dto.setHolder(paymentCard.getHolder());
        dto.setExpirationDate(paymentCard.getExpirationDate());
        dto.setActive(paymentCard.getActive());
        dto.setCreatedAt(paymentCard.getCreatedAt());
        dto.setUpdatedAt(paymentCard.getUpdatedAt());

        if (paymentCard.getUser() != null) {
            dto.setUserId(paymentCard.getUser().getId());
            dto.setUserFullName(userToFullName(paymentCard.getUser()));
        }

        return dto;
    }

    private String userToFullName(User user) {
        if (user == null) return null;
        return user.getName() + " " + user.getSurname();
    }
}