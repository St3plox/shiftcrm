package ru.tveu.shiftcrm.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.tveu.shiftcrm.api.Path;
import ru.tveu.shiftcrm.api.dto.PeriodDTO;
import ru.tveu.shiftcrm.api.dto.SellerCreateRequest;
import ru.tveu.shiftcrm.api.dto.SellerDTO;
import ru.tveu.shiftcrm.api.dto.SellerUpdateRequest;
import ru.tveu.shiftcrm.core.service.AnalysisService;
import ru.tveu.shiftcrm.core.service.SellerService;


@RestController
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;
    private final AnalysisService analysisService;

    @GetMapping(Path.SELLER_GET)
    @ResponseStatus(HttpStatus.OK)
    public SellerDTO getById(@PathVariable Long id) {

        return sellerService.get(id);
    }

    @GetMapping(Path.SELLER_GET_ALL)
    @ResponseStatus(HttpStatus.OK)
    public Page<SellerDTO> getAll(Pageable pageable) {

        return sellerService.getAll(pageable);
    }

    @PostMapping(Path.SELLER_POST)
    @ResponseStatus(HttpStatus.CREATED)
    public SellerDTO create(@RequestBody @Valid SellerCreateRequest request) {

        return sellerService.create(request);
    }

    @PutMapping(Path.SELLER_PUT)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public SellerDTO update(@RequestBody @Valid SellerUpdateRequest request) {

        return sellerService.update(request);
    }

    @DeleteMapping(Path.SELLER_DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {

        sellerService.delete(id);
    }

    //=============================================================================
    //Analysis endpoints


    @GetMapping(Path.SELLER_GET_MOST_PRODUCTIVE)
    @ResponseStatus(HttpStatus.OK)
    public SellerDTO getMostProductiveSeller(@RequestParam String startDate, @RequestParam String endDate) {

        return analysisService.getMostProductiveSeller(startDate, endDate);
    }


    @GetMapping(Path.SELLER_GET_WITH_TX_BELOW_THRESHOLD)
    @ResponseStatus(HttpStatus.OK)
    public Page<SellerDTO> getSellersWithTransactionsBelowThreshold(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam double threshold,
            Pageable pageable) {

        return analysisService.getSellersWithTransactionsBelowThreshold(startDate, endDate, threshold, pageable);
    }

    @GetMapping(Path.SELLER_GET_BEST_PERIOD)
    @ResponseStatus(HttpStatus.OK)
    public PeriodDTO getBestTransactionPeriod(@RequestParam long durationInDays, @RequestParam Long sellerId) {

        return analysisService.findBestTransactionPeriod(durationInDays, sellerId);
    }

}
