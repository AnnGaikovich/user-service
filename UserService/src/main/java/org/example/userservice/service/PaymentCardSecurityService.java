package org.example.userservice.service;

import org.example.userservice.repository.PaymentCardRepository;
import org.springframework.stereotype.Service;

@Service("paymentCardSecurityService")
public class PaymentCardSecurityService {

    private final PaymentCardRepository paymentCardRepository;

    public PaymentCardSecurityService(PaymentCardRepository paymentCardRepository) {
        this.paymentCardRepository = paymentCardRepository;
    }

    public boolean isCardOwner(Long cardId, Long userId) {
        return paymentCardRepository.findById(cardId)
                .map(card -> card.getUser().getId().equals(userId))
                .orElse(false);
    }
}