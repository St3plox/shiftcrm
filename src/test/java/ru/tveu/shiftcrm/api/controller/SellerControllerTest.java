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
import ru.tveu.shiftcrm.api.Path;
import ru.tveu.shiftcrm.api.dto.PeriodDTO;
import ru.tveu.shiftcrm.api.dto.SellerCreateRequest;
import ru.tveu.shiftcrm.api.dto.SellerDTO;
import ru.tveu.shiftcrm.api.dto.SellerUpdateRequest;
import ru.tveu.shiftcrm.core.exception.ErrorCode;
import ru.tveu.shiftcrm.core.exception.ServiceException;
import ru.tveu.shiftcrm.core.service.AnalysisService;
import ru.tveu.shiftcrm.core.service.SellerService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SellerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SellerService sellerService;

    @MockBean
    private AnalysisService analysisService;

    private SellerDTO sellerDTO;
    private SellerCreateRequest createRequest;
    private SellerUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        sellerDTO = SellerDTO.builder()
                .id(1L)
                .name("Test Seller")
                .contactInfo("test@example.com")
                .registrationDate("2024-10-20")
                .build();

        createRequest = SellerCreateRequest.builder()
                .name("Test Seller")
                .contactInfo("test@example.com")
                .build();

        updateRequest = SellerUpdateRequest.builder()
                .id(1L)
                .name("Updated Seller")
                .contactInfo("updated@example.com")
                .build();
    }

    @Test
    void testGetSellerById_Success() throws Exception {
        when(sellerService.get(1L)).thenReturn(sellerDTO);

        mockMvc.perform(get(Path.SELLER_GET, 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(sellerDTO.id()))
                .andExpect(jsonPath("$.name").value(sellerDTO.name()));

        verify(sellerService, times(1)).get(1L);
    }

    @Test
    void testGetSellerById_NotFound() throws Exception {
        when(sellerService.get(1L)).thenThrow(new ServiceException(ErrorCode.OBJECT_NOT_FOUND, "Seller not found"));

        mockMvc.perform(get(Path.SELLER_GET, 1L))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Seller not found"));

        verify(sellerService, times(1)).get(1L);
    }

    @Test
    void testGetAllSellers_Success() throws Exception {
        Page<SellerDTO> sellerPage = new PageImpl<>(List.of(sellerDTO));
        when(sellerService.getAll(any(PageRequest.class))).thenReturn(sellerPage);

        mockMvc.perform(get(Path.SELLER_GET_ALL)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalElements").value(sellerPage.getTotalElements()))
                .andExpect(jsonPath("$.content[0].id").value(sellerDTO.id()));

        verify(sellerService, times(1)).getAll(any(PageRequest.class));
    }

    @Test
    void testCreateSeller_Success() throws Exception {
        when(sellerService.create(any(SellerCreateRequest.class))).thenReturn(sellerDTO);

        mockMvc.perform(post(Path.SELLER_POST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(sellerDTO.id()));

        verify(sellerService, times(1)).create(any(SellerCreateRequest.class));
    }

    @Test
    void testCreateSeller_InvalidRequest() throws Exception {
        SellerCreateRequest invalidRequest = SellerCreateRequest.builder()
                .name("")
                .contactInfo("test@example.com")
                .build();

        mockMvc.perform(post(Path.SELLER_POST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(sellerService, times(0)).create(any(SellerCreateRequest.class));
    }

    @Test
    void testUpdateSeller_Success() throws Exception {
        when(sellerService.update(any(SellerUpdateRequest.class))).thenReturn(sellerDTO);

        mockMvc.perform(put(Path.SELLER_PUT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(sellerDTO.id()));

        verify(sellerService, times(1)).update(any(SellerUpdateRequest.class));
    }

    @Test
    void testDeleteSeller_Success() throws Exception {
        mockMvc.perform(delete(Path.SELLER_DELETE, 1L))
                .andExpect(status().isNoContent());

        verify(sellerService, times(1)).delete(1L);
    }

    @Test
    void testGetMostProductiveSeller_Success() throws Exception {
        when(analysisService.getMostProductiveSeller(anyString(), anyString())).thenReturn(sellerDTO);

        mockMvc.perform(get(Path.SELLER_GET_MOST_PRODUCTIVE)
                        .param("startDate", "2024-01-01")
                        .param("endDate", "2024-10-20"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(sellerDTO.id()));

        verify(analysisService, times(1)).getMostProductiveSeller(anyString(), anyString());
    }

    @Test
    void testGetSellersWithTransactionsBelowThreshold_Success() throws Exception {
        Page<SellerDTO> sellerPage = new PageImpl<>(List.of(sellerDTO));
        when(analysisService.getSellersWithTransactionsBelowThreshold(anyString(), anyString(), anyDouble(), any(PageRequest.class)))
                .thenReturn(sellerPage);

        mockMvc.perform(get(Path.SELLER_GET_WITH_TX_BELOW_THRESHOLD)
                        .param("startDate", "2024-01-01")
                        .param("endDate", "2024-10-20")
                        .param("threshold", "50.0")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalElements").value(sellerPage.getTotalElements()));

        verify(analysisService, times(1)).getSellersWithTransactionsBelowThreshold(anyString(), anyString(), anyDouble(), any(PageRequest.class));
    }

    @Test
    void testGetBestTransactionPeriod_Success() throws Exception {
        PeriodDTO periodDTO = new PeriodDTO("2024-01-01", "2024-10-20");
        when(analysisService.findBestTransactionPeriod(anyLong(), anyLong())).thenReturn(periodDTO);

        mockMvc.perform(get(Path.SELLER_GET_BEST_PERIOD)
                        .param("durationInDays", "30")
                        .param("sellerId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.dateStart").value(periodDTO.dateStart()))
                .andExpect(jsonPath("$.dateEnd").value(periodDTO.dateEnd()));

        verify(analysisService, times(1)).findBestTransactionPeriod(anyLong(), anyLong());
    }
}
