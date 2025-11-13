// src/main/java/com/example/app/entity/PaymentCard.java
package org.example.userservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_cards")
@EntityListeners(AuditingEntityListener.class)
public class PaymentCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User is mandatory")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @NotBlank(message = "Card number is mandatory")
    @Pattern(regexp = "^[0-9]{16,19}$")
    @Column(name = "number", nullable = false, length = 19)
    private String number;

    @NotBlank(message = "Card holder is mandatory")
    @Size(max = 100)
    @Column(name = "holder", nullable = false, length = 100)
    private String holder;

    @NotBlank(message = "Expiration date is mandatory")
    @Pattern(regexp = "^(0[1-9]|1[0-2])/[0-9]{2}$")
    @Column(name = "expiration_date", nullable = false, length = 7)
    private String expirationDate;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Конструкторы
    public PaymentCard() {}

    public PaymentCard(Long id, User user, String number, String holder, String expirationDate,
                       Boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.user = user;
        this.number = number;
        this.holder = holder;
        this.expirationDate = expirationDate;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Builder pattern (вручную)
    public static PaymentCardBuilder builder() {
        return new PaymentCardBuilder();
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

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

    // Builder класс
    public static class PaymentCardBuilder {
        private Long id;
        private User user;
        private String number;
        private String holder;
        private String expirationDate;
        private Boolean active = true;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public PaymentCardBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public PaymentCardBuilder user(User user) {
            this.user = user;
            return this;
        }

        public PaymentCardBuilder number(String number) {
            this.number = number;
            return this;
        }

        public PaymentCardBuilder holder(String holder) {
            this.holder = holder;
            return this;
        }

        public PaymentCardBuilder expirationDate(String expirationDate) {
            this.expirationDate = expirationDate;
            return this;
        }

        public PaymentCardBuilder active(Boolean active) {
            this.active = active;
            return this;
        }

        public PaymentCardBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public PaymentCardBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public PaymentCard build() {
            return new PaymentCard(id, user, number, holder, expirationDate, active, createdAt, updatedAt);
        }
    }
}