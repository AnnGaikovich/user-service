// PaymentCardRepository.java
package org.example.userservice.repository;

import org.example.userservice.entity.PaymentCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentCardRepository extends JpaRepository<PaymentCard, Long>, JpaSpecificationExecutor<PaymentCard> {

    // Named method with pagination
    Page<PaymentCard> findByUserId(Long userId, Pageable pageable);

    // Named method for finding by card number
    Optional<PaymentCard> findByNumber(String number);

    // JPQL query with join
    @Query("SELECT pc FROM PaymentCard pc JOIN pc.user u WHERE u.id = :userId AND pc.active = true")
    List<PaymentCard> findActiveCardsByUser(@Param("userId") Long userId);

    // Native SQL for counting cards per user
    @Query(value = "SELECT COUNT(*) FROM payment_cards WHERE user_id = :userId", nativeQuery = true)
    long countByUserId(@Param("userId") Long userId);

}