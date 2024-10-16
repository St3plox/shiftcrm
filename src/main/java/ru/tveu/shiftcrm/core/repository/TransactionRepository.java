package ru.tveu.shiftcrm.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.tveu.shiftcrm.core.entity.Seller;
import ru.tveu.shiftcrm.core.entity.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findAllBySeller(Seller seller, Pageable pageable);
    List<Transaction> findAllBySeller(Seller seller);

    @Query("SELECT t.seller FROM Transaction t " +
            "WHERE t.transactionDate >= :startDate AND t.transactionDate <= :endDate " +
            "GROUP BY t.seller " +
            "ORDER BY SUM(t.amount) DESC")
    Optional<Seller> findMostProductiveSeller(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT t.seller FROM Transaction t " +
            "WHERE t.transactionDate >= :startDate AND t.transactionDate <= :endDate " +
            "GROUP BY t.seller " +
            "HAVING SUM(t.amount) < :threshold")
    Page<Seller> findSellersWithTransactionsBelowThreshold(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("threshold") double threshold,
            Pageable pageable
    );
}
