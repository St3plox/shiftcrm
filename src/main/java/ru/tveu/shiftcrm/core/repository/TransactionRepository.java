package ru.tveu.shiftcrm.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.tveu.shiftcrm.core.entity.Seller;
import ru.tveu.shiftcrm.core.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findAllBySeller(Seller seller, Pageable pageable);

}
