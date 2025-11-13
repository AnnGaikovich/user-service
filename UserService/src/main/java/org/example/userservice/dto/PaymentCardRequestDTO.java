package org.example.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.io.Serializable;

public class PaymentCardRequestDTO implements Serializable {

    @NotNull(message = "User ID is mandatory")
    private Long userId;

    @NotBlank(message = "Card number is mandatory")
    @Pattern(regexp = "^[0-9]{16,19}$", message = "Card number must contain 16-19 digits")
    private String number;

    @NotBlank(message = "Card holder is mandatory")
    private String holder;

    @NotBlank(message = "Expiration date is mandatory")
    @Pattern(regexp = "^(0[1-9]|1[0-2])/[0-9]{2}$", message = "Expiration date must be in format MM/YY")
    private String expirationDate;

    private Boolean active = true;

    // Конструкторы
    public PaymentCardRequestDTO() {}

    public PaymentCardRequestDTO(Long userId, String number, String holder, String expirationDate, Boolean active) {
        this.userId = userId;
        this.number = number;
        this.holder = holder;
        this.expirationDate = expirationDate;
        this.active = active;
    }

    // Геттеры и сеттеры
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public String getHolder() { return holder; }
    public void setHolder(String holder) { this.holder = holder; }

    public String getExpirationDate() { return expirationDate; }
    public void setExpirationDate(String expirationDate) { this.expirationDate = expirationDate; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}