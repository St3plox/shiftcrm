package ru.tveu.shiftcrm.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.tveu.shiftcrm.api.Path;
import ru.tveu.shiftcrm.api.dto.TransactionCreateRequest;
import ru.tveu.shiftcrm.api.dto.TransactionDTO;
import ru.tveu.shiftcrm.core.service.TransactionService;

@RestController
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping(Path.TRANSACTION_GET)
    @ResponseStatus(HttpStatus.OK)
    public TransactionDTO getTransaction(@PathVariable Long id) {

        return transactionService.get(id);
    }

    @GetMapping(Path.TRANSACTION_GET_BY_SELLER)
    @ResponseStatus(HttpStatus.OK)
    public Page<TransactionDTO> getAllTransactionsBySeller(@RequestParam Long sellerId, Pageable pageable) {

        return transactionService.getAllBySellerId(sellerId, pageable);
    }

    @PostMapping(Path.TRANSACTION_POST)
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionDTO createTransaction(@RequestBody TransactionCreateRequest createRequest) {

        return transactionService.create(createRequest);
    }

}
