// PaymentCardRepository.java
package org.example.userservice.repository;

import org.example.userservice.entity.PaymentCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentCardRepository extends JpaRepository<PaymentCard, Long> {

    // Named method with pagination - ДОБАВЛЯЕМ ЭТОТ МЕТОД
    Page<PaymentCard> findByUserId(Long userId, Pageable pageable);

    // JPQL query with join
    @Query("SELECT pc FROM PaymentCard pc JOIN pc.user u WHERE u.id = :userId AND pc.active = true")
    List<PaymentCard> findActiveCardsByUser(@Param("userId") Long userId);

    // Native SQL query
    @Query(value = "SELECT * FROM payment_cards WHERE user_id = :userId AND active = :active", nativeQuery = true)
    List<PaymentCard> findByUserIdAndActiveStatus(@Param("userId") Long userId, @Param("active") Boolean active);

    // Native SQL for counting cards per user
    @Query(value = "SELECT COUNT(*) FROM payment_cards WHERE user_id = :userId", nativeQuery = true)
    long countByUserId(@Param("userId") Long userId);

    // Native SQL for bulk update
    @Modifying
    @Query(value = "UPDATE payment_cards SET active = :active WHERE id = :id", nativeQuery = true)
    void updateActiveStatus(@Param("id") Long id, @Param("active") Boolean active);

    // Named method for finding by card number
    Optional<PaymentCard> findByNumber(String number);
}