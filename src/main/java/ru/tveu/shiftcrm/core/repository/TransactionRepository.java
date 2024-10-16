package ru.tveu.shiftcrm.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tveu.shiftcrm.core.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
