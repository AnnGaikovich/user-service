package org.example.userservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.io.Serializable;

@Setter
@Getter
public class PaymentCardResponseDTO implements Serializable {
    // Геттеры и сеттеры
    private Long id;
    private String number;
    private String holder;
    private String expirationDate;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long userId;
    private String userFullName;

    // Конструкторы
    public PaymentCardResponseDTO() {}

    public PaymentCardResponseDTO(Long id, String number, String holder, String expirationDate,
                                  Boolean active, LocalDateTime createdAt, LocalDateTime updatedAt,
                                  Long userId, String userFullName) {
        this.id = id;
        this.number = number;
        this.holder = holder;
        this.expirationDate = expirationDate;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userId = userId;
        this.userFullName = userFullName;
    }

}