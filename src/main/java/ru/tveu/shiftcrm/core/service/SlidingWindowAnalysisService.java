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
import ru.tveu.shiftcrm.core.repository.SellerRepository;
import ru.tveu.shiftcrm.core.repository.TransactionRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SlidingWindowAnalysisService implements AnalysisService {

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

        List<Transaction> sellerTransactions = transactionRepository.findAllBySellerOrderByTransactionDateAsc(seller);

        if (sellerTransactions.isEmpty()) {
            throw new ServiceException(ErrorCode.OBJECT_NOT_FOUND, "No transactions found for seller with id: " + sellerId);
        }

        var duration = Duration.ofDays(durationInDays);

        LocalDateTime bestStart = null;
        LocalDateTime bestEnd = null;
        int maxTransactions = 0;

        int start = 0;
        for (int end = 0; end < sellerTransactions.size(); end++) {
            Transaction endTransaction = sellerTransactions.get(end);
            LocalDateTime endTime = endTransaction.getTransactionDate();


            while (start < end && Duration.between(sellerTransactions.get(start).getTransactionDate(), endTime).compareTo(duration) > 0) {
                start++;
            }

            int currentTransactionCount = end - start + 1;

            if (currentTransactionCount > maxTransactions) {
                maxTransactions = currentTransactionCount;
                bestStart = sellerTransactions.get(start).getTransactionDate();
                bestEnd = endTime;
            }
        }

        if (bestStart == null || bestEnd == null)
            throw new ServiceException(ErrorCode.OBJECT_NOT_FOUND, "There is no such period");


        return new PeriodDTO(bestStart.toString(), bestEnd.toString());
    }
}
