package org.example.userservice.controller;

import org.example.userservice.dto.PaymentCardRequestDTO;
import org.example.userservice.dto.PaymentCardResponseDTO;
import org.example.userservice.service.PaymentCardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/cards")
@Tag(name = "Payment Card Management", description = "APIs for managing payment cards")
public class PaymentCardController {

    private final PaymentCardService paymentCardService;

    public PaymentCardController(PaymentCardService paymentCardService) {
        this.paymentCardService = paymentCardService;
    }

    @Operation(summary = "Create a new payment card", description = "Creates a new payment card for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Payment card created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #cardDTO.userId == authentication.principal.userId)")
    public ResponseEntity<PaymentCardResponseDTO> createCard(@Valid @RequestBody PaymentCardRequestDTO cardDTO) {
        PaymentCardResponseDTO createdCard = paymentCardService.createCard(cardDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCard);
    }

    @Operation(summary = "Get payment card by ID", description = "Retrieves a specific payment card by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment card found"),
            @ApiResponse(responseCode = "404", description = "Payment card not found")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @paymentCardSecurityService.isCardOwner(#id, authentication.principal.userId)")
    public ResponseEntity<PaymentCardResponseDTO> getCardById(
            @Parameter(description = "ID of the payment card to be retrieved") @PathVariable Long id) {
        PaymentCardResponseDTO card = paymentCardService.getCardById(id);
        return ResponseEntity.ok(card);
    }

    @Operation(summary = "Get all payment cards", description = "Retrieves a paginated list of all payment cards with optional filtering")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<PaymentCardResponseDTO>> getAllCards(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Filter by card holder name") @RequestParam(required = false) String holder,
            @Parameter(description = "Filter by card number") @RequestParam(required = false) String number) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<PaymentCardResponseDTO> cards = paymentCardService.getAllCards(pageable, holder, number);
        return ResponseEntity.ok(cards);
    }

    @Operation(summary = "Get payment cards by user ID", description = "Retrieves all payment cards for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment cards retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #userId == authentication.principal.userId)")
    public ResponseEntity<Page<PaymentCardResponseDTO>> getCardsByUserId(
            @Parameter(description = "ID of the user") @PathVariable Long userId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "id") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<PaymentCardResponseDTO> cards = paymentCardService.getCardsByUserId(userId, pageable);
        return ResponseEntity.ok(cards);
    }

    @Operation(summary = "Update payment card", description = "Updates an existing payment card's information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment card updated successfully"),
            @ApiResponse(responseCode = "404", description = "Payment card not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @paymentCardSecurityService.isCardOwner(#id, authentication.principal.userId)")
    public ResponseEntity<PaymentCardResponseDTO> updateCard(
            @Parameter(description = "ID of the payment card to be updated") @PathVariable Long id,
            @Valid @RequestBody PaymentCardRequestDTO cardDTO) {
        PaymentCardResponseDTO updatedCard = paymentCardService.updateCard(id, cardDTO);
        return ResponseEntity.ok(updatedCard);
    }

    @Operation(summary = "Activate payment card", description = "Activates a payment card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment card activated successfully"),
            @ApiResponse(responseCode = "404", description = "Payment card not found")
    })
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN') or @paymentCardSecurityService.isCardOwner(#id, authentication.principal.userId)")
    public ResponseEntity<Map<String, String>> activateCard(
            @Parameter(description = "ID of the payment card to be activated") @PathVariable Long id) {
        paymentCardService.activateCard(id);
        return ResponseEntity.ok(Map.of("message", "Card activated successfully"));
    }

    @Operation(summary = "Deactivate payment card", description = "Deactivates a payment card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment card deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "Payment card not found")
    })
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN') or @paymentCardSecurityService.isCardOwner(#id, authentication.principal.userId)")
    public ResponseEntity<Map<String, String>> deactivateCard(
            @Parameter(description = "ID of the payment card to be deactivated") @PathVariable Long id) {
        paymentCardService.deactivateCard(id);
        return ResponseEntity.ok(Map.of("message", "Card deactivated successfully"));
    }

    @Operation(summary = "Delete payment card", description = "Deletes a payment card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment card deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Payment card not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @paymentCardSecurityService.isCardOwner(#id, authentication.principal.userId)")
    public ResponseEntity<Map<String, String>> deleteCard(
            @Parameter(description = "ID of the payment card to be deleted") @PathVariable Long id) {
        paymentCardService.deleteCard(id);
        return ResponseEntity.ok(Map.of("message", "Card deleted successfully"));
    }
}