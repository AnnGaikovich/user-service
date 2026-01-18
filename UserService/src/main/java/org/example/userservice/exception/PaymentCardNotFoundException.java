package org.example.userservice.exception;

public class PaymentCardNotFoundException extends RuntimeException {
    public PaymentCardNotFoundException(String message) {
        super(message);
    }

    public PaymentCardNotFoundException(Long id) {
        super("Payment card not found with ID: " + id);
    }
}
