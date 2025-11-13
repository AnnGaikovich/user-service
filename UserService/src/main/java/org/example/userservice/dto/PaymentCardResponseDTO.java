package org.example.userservice.dto;

import java.time.LocalDateTime;
import java.io.Serializable;

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

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public String getHolder() { return holder; }
    public void setHolder(String holder) { this.holder = holder; }

    public String getExpirationDate() { return expirationDate; }
    public void setExpirationDate(String expirationDate) { this.expirationDate = expirationDate; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserFullName() { return userFullName; }
    public void setUserFullName(String userFullName) { this.userFullName = userFullName; }
}