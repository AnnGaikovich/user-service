package org.example.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@Schema(description = "Payment card creation and update request")
public class PaymentCardRequestDTO implements Serializable {

    @Schema(description = "ID of the user who owns the card", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "User ID is mandatory")
    private Long userId;

    @Schema(description = "Card number (16-19 digits)", example = "4111111111111111", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Card number is mandatory")
    @Pattern(regexp = "^[0-9]{16,19}$", message = "Card number must contain 16-19 digits")
    private String number;

    @Schema(description = "Card holder name", example = "JOHN DOE", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Card holder is mandatory")
    private String holder;

    @Schema(description = "Expiration date in MM/YY format", example = "12/25", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Expiration date is mandatory")
    @Pattern(regexp = "^(0[1-9]|1[0-2])/[0-9]{2}$", message = "Expiration date must be in format MM/YY")
    private String expirationDate;

    @Schema(description = "Whether the card is active", example = "true")
    private Boolean active = true;

    public PaymentCardRequestDTO() {}

}