package ru.tveu.shiftcrm.core.repository;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import ru.tveu.shiftcrm.core.entity.PaymentType;
import ru.tveu.shiftcrm.core.entity.Seller;
import ru.tveu.shiftcrm.core.entity.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private SellerRepository sellerRepository;

    private Seller seller1;
    private Seller seller2;

    @BeforeEach
    void setUp() {
        seller1 = Seller.builder()
                .name("Seller 1")
                .contactInfo("seller1@example.com")
                .build();

        seller2 = Seller.builder()
                .name("Seller 2")
                .contactInfo("seller2@example.com")
                .build();

        sellerRepository.save(seller1);
        sellerRepository.save(seller2);

        createTransaction(seller1, 100.0, PaymentType.CARD, LocalDateTime.now().minusDays(2));
        createTransaction(seller1, 200.0, PaymentType.CASH, LocalDateTime.now().minusHours(2));
        createTransaction(seller2, 150.0, PaymentType.CARD, LocalDateTime.now().minusDays(3));
    }

    private void createTransaction(Seller seller, double amount, PaymentType paymentType, LocalDateTime transactionDate) {
        Transaction transaction = Transaction.builder()
                .seller(seller)
                .amount(amount)
                .paymentType(paymentType)
                .transactionDate(transactionDate)
                .build();
        transactionRepository.save(transaction);
    }

    @Test
    void testFindAllBySeller() {
        Pageable pageable = Pageable.ofSize(10);
        Page<Transaction> result = transactionRepository.findAllBySeller(seller1, pageable);
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    public void testFindAllBySellerOrderByTransactionDateAsc() {
        List<Transaction> result = transactionRepository.findAllBySellerOrderByTransactionDateAsc(seller1);
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getAmount()).isEqualTo(100.0);
    }

    @Test
    public void testFindMostProductiveSeller() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(4);
        LocalDateTime endDate = LocalDateTime.now();
        Optional<Seller> result = transactionRepository.findMostProductiveSeller(startDate, endDate);
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(seller1);
    }

    @Test
    public void testFindSellersWithTransactionsBelowThreshold() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(4);
        LocalDateTime endDate = LocalDateTime.now();
        double threshold = 250.0;
        Page<Seller> result = transactionRepository.findSellersWithTransactionsBelowThreshold(startDate, endDate, threshold, Pageable.ofSize(10));
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).contains(seller2);
    }

    @Test
    public void testFindNoSellersAboveThreshold() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(4);
        LocalDateTime endDate = LocalDateTime.now();
        double threshold = 50.0;
        Page<Seller> result = transactionRepository.findSellersWithTransactionsBelowThreshold(startDate, endDate, threshold, Pageable.ofSize(10));
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    public void testFindSellersWithExactThreshold() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(4);
        LocalDateTime endDate = LocalDateTime.now();
        double threshold = 150.0;
        Page<Seller> result = transactionRepository.findSellersWithTransactionsBelowThreshold(startDate, endDate, threshold, Pageable.ofSize(10));
        assertThat(result.getContent()).doesNotContain(seller2);
    }

    @Test
    public void testEmptyTransactionListForSeller() {
        Seller newSeller = Seller.builder().name("New Seller").contactInfo("new_seller@example.com").build();
        sellerRepository.save(newSeller);

        List<Transaction> result = transactionRepository.findAllBySellerOrderByTransactionDateAsc(newSeller);
        assertThat(result).isEmpty();
    }

    @Test
    public void testNoSellersWithTransactions() {
        LocalDateTime startDate = LocalDateTime.now().minusYears(10);
        LocalDateTime endDate = LocalDateTime.now().minusYears(5);
        Page<Seller> result = transactionRepository.findSellersWithTransactionsBelowThreshold(startDate, endDate, 100.0, Pageable.ofSize(10));
        assertThat(result.getContent()).isEmpty();
    }
}

