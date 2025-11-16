package org.example.userservice.mapper;

import org.example.userservice.dto.PaymentCardRequestDTO;
import org.example.userservice.dto.PaymentCardResponseDTO;
import org.example.userservice.entity.PaymentCard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface PaymentCardMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    PaymentCard toEntity(PaymentCardRequestDTO paymentCardRequestDTO);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "number", source = "number", qualifiedByName = "maskCardNumber")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userFullName", expression = "java(paymentCard.getUser().getName() + \" \" + paymentCard.getUser().getSurname())")
    PaymentCardResponseDTO toResponseDTO(PaymentCard paymentCard);

    @Named("maskCardNumber")
    default String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 12) {
            return cardNumber;
        }
        String firstFour = cardNumber.substring(0, 4);
        String lastFour = cardNumber.substring(cardNumber.length() - 4);
        return firstFour + "********" + lastFour;
    }
}