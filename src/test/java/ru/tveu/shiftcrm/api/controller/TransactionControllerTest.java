package ru.tveu.shiftcrm.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.tveu.shiftcrm.api.Path;
import ru.tveu.shiftcrm.api.dto.TransactionCreateRequest;
import ru.tveu.shiftcrm.api.dto.TransactionDTO;
import ru.tveu.shiftcrm.core.entity.PaymentType;
import ru.tveu.shiftcrm.core.exception.ErrorCode;
import ru.tveu.shiftcrm.core.exception.ServiceException;
import ru.tveu.shiftcrm.core.service.TransactionService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionService transactionService;

    private TransactionDTO transactionDTO;
    private TransactionCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        transactionDTO = TransactionDTO.builder()
                .id(1L)
                .amount(100.0)
                .sellerId(1L)
                .paymentType(PaymentType.CARD)
                .build();

        createRequest = TransactionCreateRequest.builder()
                .sellerId(1L)
                .amount(100.0)
                .paymentType(PaymentType.CARD.toString())
                .build();
    }

    @Test
    void testGetTransaction_Success() throws Exception {
        when(transactionService.get(1L)).thenReturn(transactionDTO);

        mockMvc.perform(get(Path.TRANSACTION_GET, 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(transactionDTO.id()))
                .andExpect(jsonPath("$.amount").value(transactionDTO.amount()));

        verify(transactionService, times(1)).get(1L);
    }

    @Test
    void testGetTransaction_NotFound() throws Exception {
        when(transactionService.get(1L)).thenThrow(new ServiceException(ErrorCode.OBJECT_NOT_FOUND, "Transaction not found"));

        mockMvc.perform(get(Path.TRANSACTION_GET, 1L))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Transaction not found"));

        verify(transactionService, times(1)).get(1L);
    }

    @Test
    void testGetAllTransactionsBySeller_Success() throws Exception {
        Page<TransactionDTO> transactionPage = new PageImpl<>(List.of(transactionDTO));
        when(transactionService.getAllBySellerId(1L, PageRequest.of(0, 10))).thenReturn(transactionPage);

        mockMvc.perform(get(Path.TRANSACTION_GET_BY_SELLER)
                        .param("sellerId", "1")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalElements").value(transactionPage.getTotalElements()))
                .andExpect(jsonPath("$.content[0].id").value(transactionDTO.id()));

        verify(transactionService, times(1)).getAllBySellerId(1L, PageRequest.of(0, 10));
    }

    @Test
    void testGetAllTransactionsBySeller_SellerNotFound() throws Exception {
        when(transactionService.getAllBySellerId(1L, PageRequest.of(0, 10)))
                .thenThrow(new ServiceException(ErrorCode.OBJECT_NOT_FOUND, "Seller not found"));

        mockMvc.perform(get(Path.TRANSACTION_GET_BY_SELLER)
                        .param("sellerId", "1")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Seller not found"));

        verify(transactionService, times(1)).getAllBySellerId(1L, PageRequest.of(0, 10));
    }

    @Test
    void testCreateTransaction_Success() throws Exception {
        when(transactionService.create(any(TransactionCreateRequest.class))).thenReturn(transactionDTO);

        mockMvc.perform(MockMvcRequestBuilders.post(Path.TRANSACTION_POST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(transactionDTO.id()));

        verify(transactionService, times(1)).create(any(TransactionCreateRequest.class));
    }

    @Test
    void testCreateTransaction_InvalidRequest() throws Exception {
        TransactionCreateRequest invalidRequest = TransactionCreateRequest.builder()
                .sellerId(null)
                .amount(100.0)
                .paymentType(PaymentType.CARD.toString())
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post(Path.TRANSACTION_POST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(transactionService, times(0)).create(any(TransactionCreateRequest.class));
    }
}