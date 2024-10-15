package ru.tveu.shiftcrm.core.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import ru.tveu.shiftcrm.core.entity.Transaction;

public interface TransactionRepository extends PagingAndSortingRepository<Transaction, Long> {
}
