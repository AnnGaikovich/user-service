package org.example.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
public class PaymentCardResponseDTO implements Serializable {

    private Long id;
    private String number;
    private String holder;
    private String expirationDate;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long userId;
    private String userFullName;

    public PaymentCardResponseDTO() {}

}