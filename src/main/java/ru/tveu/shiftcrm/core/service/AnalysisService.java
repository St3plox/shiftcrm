package ru.tveu.shiftcrm.core.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.tveu.shiftcrm.api.dto.PeriodDTO;
import ru.tveu.shiftcrm.api.dto.SellerDTO;

public interface AnalysisService {

    SellerDTO getMostProductiveSeller(String startDate, String endDate);

    Page<SellerDTO> getSellersWithTransactionsBelowThreshold(String startDate, String endDate, double txSum, Pageable pageable);

    PeriodDTO findBestTransactionPeriod(long durationInDays, Long sellerId);
}
