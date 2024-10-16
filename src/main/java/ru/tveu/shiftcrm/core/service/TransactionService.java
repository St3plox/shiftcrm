package ru.tveu.shiftcrm.core.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.tveu.shiftcrm.api.dto.TransactionCreateRequest;
import ru.tveu.shiftcrm.api.dto.TransactionDTO;

public interface TransactionService {

    TransactionDTO create(TransactionCreateRequest createRequest);

    TransactionDTO get(Long id);

    Page<TransactionDTO> getAllBySellerId(Long sellerId, Pageable pageable);

}
