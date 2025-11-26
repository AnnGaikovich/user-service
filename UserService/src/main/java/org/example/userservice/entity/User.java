package org.example.userservice.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.example.userservice.exception.BusinessRuleException;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User {

    public static final int MAX_PAYMENT_CARDS = 5;

    // Геттеры и сеттеры
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is mandatory")
    @Size(max = 100)
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Surname is mandatory")
    @Size(max = 100)
    @Column(name = "surname", nullable = false, length = 100)
    private String surname;

    @NotNull(message = "Birth date is mandatory")
    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Email
    @NotBlank(message = "Email is mandatory")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<PaymentCard> paymentCards = new ArrayList<>();

    // Конструкторы
    public User() {}

    public User(Long id, String name, String surname, LocalDate birthDate, String email, Boolean active,
                LocalDateTime createdAt, LocalDateTime updatedAt, List<PaymentCard> paymentCards) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.birthDate = birthDate;
        this.email = email;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.paymentCards = paymentCards;
    }

    // Builder pattern (вручную)
    public static UserBuilder builder() {
        return new UserBuilder();
    }

    // Не более MAX_PAYMENT_CARDS карт
    public void addPaymentCard(PaymentCard card) {
        if (paymentCards.size() >= MAX_PAYMENT_CARDS) {
            throw new BusinessRuleException("User cannot have more than " + MAX_PAYMENT_CARDS + " payment cards");
        }
        paymentCards.add(card);
        card.setUser(this);
    }

    public void removePaymentCard(PaymentCard card) {
        paymentCards.remove(card);
        card.setUser(null);
    }

    // Builder класс
    public static class UserBuilder {
        private Long id;
        private String name;
        private String surname;
        private LocalDate birthDate;
        private String email;
        private Boolean active = true;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<PaymentCard> paymentCards = new ArrayList<>();

        public UserBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UserBuilder name(String name) {
            this.name = name;
            return this;
        }

        public UserBuilder surname(String surname) {
            this.surname = surname;
            return this;
        }

        public UserBuilder birthDate(LocalDate birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        public UserBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserBuilder active(Boolean active) {
            this.active = active;
            return this;
        }

        public UserBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public UserBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public UserBuilder paymentCards(List<PaymentCard> paymentCards) {
            this.paymentCards = paymentCards;
            return this;
        }

        public User build() {
            return new User(id, name, surname, birthDate, email, active, createdAt, updatedAt, paymentCards);
        }
    }
}