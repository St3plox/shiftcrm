package ru.tveu.shiftcrm.core.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ru.tveu.shiftcrm.api.dto.PeriodDTO;
import ru.tveu.shiftcrm.api.dto.SellerDTO;
import ru.tveu.shiftcrm.core.entity.Seller;
import ru.tveu.shiftcrm.core.entity.Transaction;
import ru.tveu.shiftcrm.core.exception.ErrorCode;
import ru.tveu.shiftcrm.core.exception.ServiceException;
import ru.tveu.shiftcrm.core.mapper.SellerMapper;
import ru.tveu.shiftcrm.core.repository.SellerRepository;
import ru.tveu.shiftcrm.core.repository.TransactionRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SlidingWindowAnalysisServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private SellerMapper sellerMapper;

    @Mock
    private SellerRepository sellerRepository;

    @InjectMocks
    private SlidingWindowAnalysisService slidingWindowAnalysisService;

    private Seller seller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        seller = new Seller();
        seller.setId(1L);
    }

    @Test
    void testGetMostProductiveSeller() {
        LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 1, 31, 23, 59);
        String startDateString = startDate.toString();
        String endDateString = endDate.toString();

        seller.setName("John Doe");
        seller.setContactInfo("john.doe@example.com");
        seller.setRegistrationDate(LocalDateTime.now());

        SellerDTO mockSellerDTO = new SellerDTO(seller.getId(), seller.getName(), seller.getContactInfo(), seller.getRegistrationDate().toString());

        when(transactionRepository.findMostProductiveSeller(startDate, endDate)).thenReturn(Optional.of(seller));
        when(sellerMapper.map(seller)).thenReturn(mockSellerDTO);


        SellerDTO result = slidingWindowAnalysisService.getMostProductiveSeller(startDateString, endDateString);

        assertNotNull(result);
        assertEquals(mockSellerDTO.id(), result.id());
        assertEquals(mockSellerDTO.name(), result.name());
        verify(transactionRepository).findMostProductiveSeller(startDate, endDate);
        verify(sellerMapper).map(seller);
    }

    @Test
    void testGetMostProductiveSellerNotFound() {
        LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 1, 31, 23, 59);
        String startDateString = startDate.toString();
        String endDateString = endDate.toString();

        when(transactionRepository.findMostProductiveSeller(startDate, endDate)).thenReturn(Optional.empty());


        ServiceException exception = assertThrows(ServiceException.class, () -> {
            slidingWindowAnalysisService.getMostProductiveSeller(startDateString, endDateString);
        });

        assertEquals(ErrorCode.OBJECT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void testGetSellersWithTransactionsBelowThreshold() {
        // Arrange
        LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 1, 31, 23, 59);
        String startDateString = startDate.toString();
        String endDateString = endDate.toString();
        double txSum = 100.0;

        Pageable pageable = Pageable.ofSize(10);
        Page<Seller> sellerPage = mock(Page.class);

        when(transactionRepository.findSellersWithTransactionsBelowThreshold(startDate, endDate, txSum, pageable))
                .thenReturn(sellerPage);
        when(sellerMapper.map(sellerPage)).thenReturn(mock(Page.class));


        Page<SellerDTO> result = slidingWindowAnalysisService.getSellersWithTransactionsBelowThreshold(startDateString, endDateString, txSum, pageable);

        assertNotNull(result);
        verify(transactionRepository).findSellersWithTransactionsBelowThreshold(startDate, endDate, txSum, pageable);
        verify(sellerMapper).map(sellerPage);
    }

    @Test
    void testFindBestTransactionPeriod() {
        long durationInDays = 7;
        Long sellerId = 1L;
        Transaction transaction1 = new Transaction(); // Add necessary fields and values
        transaction1.setTransactionDate(LocalDateTime.of(2024, 1, 1, 0, 0));
        Transaction transaction2 = new Transaction();
        transaction2.setTransactionDate(LocalDateTime.of(2024, 1, 2, 0, 0));
        List<Transaction> transactions = List.of(transaction1, transaction2);

        when(sellerRepository.findById(sellerId)).thenReturn(Optional.of(seller));
        when(transactionRepository.findAllBySellerOrderByTransactionDateAsc(seller)).thenReturn(transactions);

        PeriodDTO result = slidingWindowAnalysisService.findBestTransactionPeriod(durationInDays, sellerId);

        assertNotNull(result);
        assertNotNull(result.dateStart());
        assertNotNull(result.dateEnd());
        verify(sellerRepository).findById(sellerId);
        verify(transactionRepository).findAllBySellerOrderByTransactionDateAsc(seller);
    }

    @Test
    void testFindBestTransactionPeriodSellerNotFound() {
        long durationInDays = 7;
        Long sellerId = 1L;

        when(sellerRepository.findById(sellerId)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            slidingWindowAnalysisService.findBestTransactionPeriod(durationInDays, sellerId);
        });

        assertEquals(ErrorCode.OBJECT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void testFindBestTransactionPeriodNoTransactions() {

        long durationInDays = 7;
        Long sellerId = 1L;

        when(sellerRepository.findById(sellerId)).thenReturn(Optional.of(seller));
        when(transactionRepository.findAllBySellerOrderByTransactionDateAsc(seller)).thenReturn(Collections.emptyList());

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            slidingWindowAnalysisService.findBestTransactionPeriod(durationInDays, sellerId);
        });

        assertEquals(ErrorCode.OBJECT_NOT_FOUND, exception.getErrorCode());
    }
}
