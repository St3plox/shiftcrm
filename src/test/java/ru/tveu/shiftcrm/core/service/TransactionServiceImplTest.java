package ru.tveu.shiftcrm.core.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.tveu.shiftcrm.api.dto.TransactionCreateRequest;
import ru.tveu.shiftcrm.api.dto.TransactionDTO;
import ru.tveu.shiftcrm.core.entity.PaymentType;
import ru.tveu.shiftcrm.core.entity.Seller;
import ru.tveu.shiftcrm.core.entity.Transaction;
import ru.tveu.shiftcrm.core.exception.ErrorCode;
import ru.tveu.shiftcrm.core.exception.ServiceException;
import ru.tveu.shiftcrm.core.mapper.TransactionMapper;
import ru.tveu.shiftcrm.core.repository.SellerRepository;
import ru.tveu.shiftcrm.core.repository.TransactionRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private SellerRepository sellerRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Seller seller;
    private Transaction transaction;
    private TransactionDTO transactionDTO;
    private TransactionCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        seller = Seller.builder()
                .id(1L)
                .name("Test Seller")
                .contactInfo("test@example.com")
                .build();

        transaction = Transaction.builder()
                .id(1L)
                .seller(seller)
                .amount(100.0)
                .paymentType(PaymentType.CARD)
                .build();

        transactionDTO = TransactionDTO.builder()
                .id(1L)
                .sellerId(seller.getId())
                .amount(100.0)
                .paymentType(PaymentType.CARD)
                .build();

        createRequest = TransactionCreateRequest.builder()
                .sellerId(seller.getId())
                .amount(100.0)
                .paymentType(PaymentType.CARD.toString())
                .build();
    }

    @Test
    void testCreateTransaction_Success() {
        when(transactionMapper.map(createRequest)).thenReturn(transaction);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        when(transactionMapper.map(transaction)).thenReturn(transactionDTO);

        TransactionDTO result = transactionService.create(createRequest);

        assertNotNull(result);
        assertEquals(transactionDTO, result);
        verify(transactionRepository, times(1)).save(transaction);
    }

    @Test
    void testGetTransactionById_Success() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
        when(transactionMapper.map(transaction)).thenReturn(transactionDTO);

        TransactionDTO result = transactionService.get(1L);

        assertNotNull(result);
        assertEquals(transactionDTO, result);
        verify(transactionRepository, times(1)).findById(1L);
    }

    @Test
    void testGetTransactionById_NotFound_ShouldThrowException() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            transactionService.get(1L);
        });

        assertEquals(ErrorCode.OBJECT_NOT_FOUND, exception.getErrorCode());
        verify(transactionRepository, times(1)).findById(1L);
    }

    @Test
    void testGetAllTransactionsBySellerId_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Transaction> transactionPage = new PageImpl<>(List.of(transaction));

        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));
        when(transactionRepository.findAllBySeller(seller, pageable)).thenReturn(transactionPage);
        when(transactionMapper.map(transactionPage)).thenReturn(new PageImpl<>(List.of(transactionDTO)));

        Page<TransactionDTO> result = transactionService.getAllBySellerId(1L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(transactionDTO, result.getContent().get(0));
        verify(sellerRepository, times(1)).findById(1L);
        verify(transactionRepository, times(1)).findAllBySeller(seller, pageable);
    }

    @Test
    void testGetAllTransactionsBySellerId_SellerNotFound_ShouldThrowException() {
        Pageable pageable = PageRequest.of(0, 10);

        when(sellerRepository.findById(1L)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            transactionService.getAllBySellerId(1L, pageable);
        });

        assertEquals(ErrorCode.OBJECT_NOT_FOUND, exception.getErrorCode());
        verify(transactionRepository, times(0)).findAllBySeller(any(Seller.class), any(Pageable.class));
    }
}