package ru.tveu.shiftcrm.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.tveu.shiftcrm.api.dto.TransactionCreateRequest;
import ru.tveu.shiftcrm.api.dto.TransactionDTO;
import ru.tveu.shiftcrm.core.entity.Seller;
import ru.tveu.shiftcrm.core.entity.Transaction;
import ru.tveu.shiftcrm.core.exception.ErrorCode;
import ru.tveu.shiftcrm.core.exception.ErrorMessage;
import ru.tveu.shiftcrm.core.exception.ServiceException;
import ru.tveu.shiftcrm.core.mapper.TransactionMapper;
import ru.tveu.shiftcrm.core.repository.SellerRepository;
import ru.tveu.shiftcrm.core.repository.TransactionRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final SellerRepository sellerRepository;

    @Override
    public TransactionDTO create(TransactionCreateRequest createRequest) {
        log.info("Creating a new transaction");

        var transaction = transactionMapper.map(createRequest);
        var savedTransaction = transactionRepository.save(transaction);

        log.info("Transaction created successfully with ID: {}", savedTransaction.getId());
        return transactionMapper.map(savedTransaction);
    }

    @Override
    public TransactionDTO get(Long id) {
        log.info("Fetching transaction with ID: {}", id);

        var transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ServiceException(ErrorCode.OBJECT_NOT_FOUND, ErrorMessage.TRANSACTION_NOT_FOUND + id));

        log.info("Transaction with ID {} retrieved successfully", id);
        return transactionMapper.map(transaction);
    }

    @Override
    public Page<TransactionDTO> getAllBySellerId(Long sellerId, Pageable pageable) {
        log.info("Fetching transactions for seller with ID: {}", sellerId);

        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ServiceException(ErrorCode.OBJECT_NOT_FOUND,
                        ErrorMessage.SELLER_NOT_FOUND + sellerId));

        Page<Transaction> transactions = transactionRepository.findAllBySeller(seller, pageable);

        log.info("Transactions for seller with ID {} retrieved successfully, total: {}", sellerId, transactions.getTotalElements());
        return transactionMapper.map(transactions);
    }
}
