package ru.tveu.shiftcrm.core.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import ru.tveu.shiftcrm.api.dto.TransactionCreateRequest;
import ru.tveu.shiftcrm.api.dto.TransactionDTO;
import ru.tveu.shiftcrm.core.entity.Seller;
import ru.tveu.shiftcrm.core.entity.Transaction;
import ru.tveu.shiftcrm.core.exception.ErrorCode;
import ru.tveu.shiftcrm.core.exception.ErrorMessage;
import ru.tveu.shiftcrm.core.exception.ServiceException;
import ru.tveu.shiftcrm.core.repository.SellerRepository;

@Component
@RequiredArgsConstructor
public class TransactionMapper {

    private final SellerRepository sellerRepository;

    public Transaction map(TransactionCreateRequest createRequest) {

        Seller seller = sellerRepository.findById(createRequest.sellerId())
                .orElseThrow(() -> new ServiceException(ErrorCode.OBJECT_NOT_FOUND,
                        ErrorMessage.SELLER_NOT_FOUND + createRequest.sellerId()));

        return Transaction.builder()
                .seller(seller)
                .amount(createRequest.amount())
                .paymentType(createRequest.paymentType())
                .build();
    }

    public TransactionDTO map(Transaction transaction) {

        return TransactionDTO.builder()
                .id(transaction.getId())
                .sellerId(transaction.getSeller().getId())
                .amount(transaction.getAmount())
                .paymentType(transaction.getPaymentType())
                .transactionDate(transaction.getTransactionDate().toString())
                .build();
    }

    public Page<TransactionDTO> map(Page<Transaction> transactions) {
        return transactions.map(this::map);
    }

}
