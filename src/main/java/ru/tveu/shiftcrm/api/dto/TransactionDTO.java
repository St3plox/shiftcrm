package ru.tveu.shiftcrm.api.dto;

import lombok.Builder;
import ru.tveu.shiftcrm.core.entity.PaymentType;

@Builder
public record TransactionDTO(

        Long id,

        Long sellerId,

        double amount,

        PaymentType paymentType,

        String transactionDate
) {
}
