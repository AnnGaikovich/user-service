package org.example.userservice.controller;

import org.example.userservice.dto.PaymentCardRequestDTO;
import org.example.userservice.dto.PaymentCardResponseDTO;
import org.example.userservice.service.PaymentCardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/cards")
public class PaymentCardController {

    private final PaymentCardService paymentCardService;

    public PaymentCardController(PaymentCardService paymentCardService) {
        this.paymentCardService = paymentCardService;
    }

    @PostMapping
    public ResponseEntity<PaymentCardResponseDTO> createCard(@Valid @RequestBody PaymentCardRequestDTO cardDTO) {
        PaymentCardResponseDTO createdCard = paymentCardService.createCard(cardDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCard);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentCardResponseDTO> getCardById(@PathVariable Long id) {
        PaymentCardResponseDTO card = paymentCardService.getCardById(id);
        return ResponseEntity.ok(card);
    }

    @GetMapping
    public ResponseEntity<Page<PaymentCardResponseDTO>> getAllCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<PaymentCardResponseDTO> cards = paymentCardService.getAllCards(pageable);
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<PaymentCardResponseDTO>> getCardsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<PaymentCardResponseDTO> cards = paymentCardService.getCardsByUserId(userId, pageable);
        return ResponseEntity.ok(cards);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentCardResponseDTO> updateCard(
            @PathVariable Long id,
            @Valid @RequestBody PaymentCardRequestDTO cardDTO) {
        PaymentCardResponseDTO updatedCard = paymentCardService.updateCard(id, cardDTO);
        return ResponseEntity.ok(updatedCard);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Map<String, String>> activateCard(@PathVariable Long id) {
        paymentCardService.activateCard(id);
        return ResponseEntity.ok(Map.of("message", "Card activated successfully"));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Map<String, String>> deactivateCard(@PathVariable Long id) {
        paymentCardService.deactivateCard(id);
        return ResponseEntity.ok(Map.of("message", "Card deactivated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteCard(@PathVariable Long id) {
        paymentCardService.deleteCard(id);
        return ResponseEntity.ok(Map.of("message", "Card deleted successfully"));
    }
}