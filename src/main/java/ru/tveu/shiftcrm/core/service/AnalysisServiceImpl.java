package ru.tveu.shiftcrm.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.tveu.shiftcrm.api.dto.PeriodDTO;
import ru.tveu.shiftcrm.api.dto.SellerDTO;
import ru.tveu.shiftcrm.core.entity.Seller;
import ru.tveu.shiftcrm.core.entity.Transaction;
import ru.tveu.shiftcrm.core.exception.ErrorCode;
import ru.tveu.shiftcrm.core.exception.ErrorMessage;
import ru.tveu.shiftcrm.core.exception.ServiceException;
import ru.tveu.shiftcrm.core.mapper.SellerMapper;
import ru.tveu.shiftcrm.core.mapper.TransactionMapper;
import ru.tveu.shiftcrm.core.repository.SellerRepository;
import ru.tveu.shiftcrm.core.repository.TransactionRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalysisServiceImpl implements AnalysisService {

    private final TransactionRepository transactionRepository;
    private final SellerMapper sellerMapper;
    private final SellerRepository sellerRepository;

    @Override
    public SellerDTO getMostProductiveSeller(String startDate, String endDate) {
        log.info("Getting most productive seller");

        LocalDateTime startTime = LocalDateTime.parse(startDate);
        LocalDateTime endTime = LocalDateTime.parse(endDate);

        Seller seller = transactionRepository.findMostProductiveSeller(startTime, endTime)
                .orElseThrow(() -> new ServiceException(ErrorCode.OBJECT_NOT_FOUND, "there is no sellers in this period"));


        log.info("Got most productive seller(id): {}", seller.getId());
        return sellerMapper.map(seller);
    }

    @Override
    public Page<SellerDTO> getSellersWithTransactionsBelowThreshold(String startDate, String endDate, double txSum, Pageable pageable) {
        log.info("Getting sellers with below threshold");

        LocalDateTime startTime = LocalDateTime.parse(startDate);
        LocalDateTime endTime = LocalDateTime.parse(endDate);

        Page<Seller> sellers = transactionRepository.findSellersWithTransactionsBelowThreshold(startTime, endTime, txSum, pageable);

        log.info("Got sellers with below threshold");
        return sellerMapper.map(sellers);
    }

    @Override
    public PeriodDTO findBestTransactionPeriod(long durationInDays, Long sellerId) {
        log.info("Getting best transaction period");

        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ServiceException(ErrorCode.OBJECT_NOT_FOUND, ErrorMessage.SELLER_NOT_FOUND));

        //TODO: make in asc
        List<Transaction> sellerTransactions = transactionRepository.findAllBySeller(seller);

        //TODO: implement sliding window

        var duration = Duration.of(3, ChronoUnit.DAYS);

        return null;
    }
}
