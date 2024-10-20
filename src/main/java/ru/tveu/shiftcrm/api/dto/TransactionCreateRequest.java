package ru.tveu.shiftcrm.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record TransactionCreateRequest(

        @NotNull(message = "sellerId must not be null")
        Long sellerId,

        double amount,

        @NotBlank
        String paymentType
) {
}
