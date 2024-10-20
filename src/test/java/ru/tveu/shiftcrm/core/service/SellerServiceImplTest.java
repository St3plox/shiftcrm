package ru.tveu.shiftcrm.core.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.tveu.shiftcrm.api.dto.SellerCreateRequest;
import ru.tveu.shiftcrm.api.dto.SellerDTO;
import ru.tveu.shiftcrm.api.dto.SellerUpdateRequest;
import ru.tveu.shiftcrm.core.entity.Seller;
import ru.tveu.shiftcrm.core.exception.ErrorCode;
import ru.tveu.shiftcrm.core.exception.ErrorMessage;
import ru.tveu.shiftcrm.core.exception.ServiceException;
import ru.tveu.shiftcrm.core.mapper.SellerMapper;
import ru.tveu.shiftcrm.core.repository.SellerRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SellerServiceImplTest {

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private SellerMapper sellerMapper;

    @InjectMocks
    private SellerServiceImpl sellerService;

    private Seller seller;
    private SellerCreateRequest createRequest;
    private SellerUpdateRequest updateRequest;
    private SellerDTO sellerDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        seller = Seller.builder()
                .id(1L)
                .name("Test Seller")
                .contactInfo("test@example.com")
                .registrationDate(LocalDateTime.now())
                .build();

        createRequest = new SellerCreateRequest("Test Seller", "test@example.com");

        updateRequest = new SellerUpdateRequest(1L, "Updated Seller", "updated@example.com");

        sellerDTO = new SellerDTO(1L, "Test Seller", "test@example.com", seller.getRegistrationDate().toString());
    }

    @Test
    void testCreateSeller_Success() {
        when(sellerMapper.map(createRequest)).thenReturn(seller);
        when(sellerRepository.save(seller)).thenReturn(seller);
        when(sellerMapper.map(seller)).thenReturn(sellerDTO);

        SellerDTO result = sellerService.create(createRequest);

        assertEquals(sellerDTO, result);
        verify(sellerRepository, times(1)).save(seller);
    }

    @Test
    void testUpdateSeller_Success() {
        when(sellerRepository.findById(updateRequest.id())).thenReturn(Optional.of(seller));
        when(sellerRepository.save(any(Seller.class))).thenReturn(seller);
        when(sellerMapper.map(any(Seller.class))).thenReturn(sellerDTO);

        SellerDTO result = sellerService.update(updateRequest);

        assertEquals(sellerDTO, result);
        verify(sellerRepository, times(1)).save(seller);
    }

    @Test
    void testGetSeller_Success() {
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));
        when(sellerMapper.map(seller)).thenReturn(sellerDTO);

        SellerDTO result = sellerService.get(1L);

        assertEquals(sellerDTO, result);
        assertEquals(seller.getRegistrationDate().toString(), result.registrationDate());
        verify(sellerRepository, times(1)).findById(1L);
    }

    @Test
    void testGetSeller_NotFound() {
        when(sellerRepository.findById(1L)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            sellerService.get(1L);
        });

        assertEquals(ErrorCode.OBJECT_NOT_FOUND, exception.getErrorCode());
        assertTrue(exception.getMessage().contains(ErrorMessage.SELLER_NOT_FOUND));
    }

    @Test
    void testGetAllSellers() {
        Page<Seller> sellerPage = new PageImpl<>(List.of(seller));
        when(sellerRepository.findAll(any(Pageable.class))).thenReturn(sellerPage);
        when(sellerMapper.map(sellerPage)).thenReturn(new PageImpl<>(List.of(sellerDTO)));

        Page<SellerDTO> result = sellerService.getAll(Pageable.ofSize(10));

        assertEquals(1, result.getTotalElements());
        assertEquals(sellerDTO, result.getContent().get(0));
        verify(sellerRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void testDeleteSeller_Success() {
        when(sellerRepository.existsById(1L)).thenReturn(true);

        sellerService.delete(1L);

        verify(sellerRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteSeller_NotFound() {
        when(sellerRepository.existsById(1L)).thenReturn(false);

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            sellerService.delete(1L);
        });

        assertEquals(ErrorCode.OBJECT_NOT_FOUND, exception.getErrorCode());
        assertTrue(exception.getMessage().contains(ErrorMessage.SELLER_NOT_FOUND));
    }

}
