package ru.tveu.shiftcrm.core.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.tveu.shiftcrm.api.dto.SellerCreateRequest;
import ru.tveu.shiftcrm.api.dto.SellerDTO;
import ru.tveu.shiftcrm.api.dto.SellerUpdateRequest;

public interface SellerService {

    SellerDTO create(SellerCreateRequest createRequest);

    SellerDTO update(SellerUpdateRequest updateRequest);

    SellerDTO get(Long id);

    Page<SellerDTO> getAll(Pageable pageable);

    void delete(Long id);
}
