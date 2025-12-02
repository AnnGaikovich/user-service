package org.example.userservice.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.io.Serializable;

@Setter
@Getter
public class UserResponseDTO implements Serializable {

    private Long id;
    private String name;
    private String surname;
    private LocalDate birthDate;
    private String email;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<PaymentCardResponseDTO> paymentCards;
}