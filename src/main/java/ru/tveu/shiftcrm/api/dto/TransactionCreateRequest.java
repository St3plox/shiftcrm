package ru.tveu.shiftcrm.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import ru.tveu.shiftcrm.core.entity.PaymentType;

@Builder
public record TransactionCreateRequest(

        @NotNull(message = "sellerId must not be null")
        Long sellerId,

        double amount,

        @NotBlank
        PaymentType paymentType
) {
}
