package ru.tveu.shiftcrm.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.tveu.shiftcrm.api.dto.SellerCreateRequest;
import ru.tveu.shiftcrm.api.dto.SellerDTO;
import ru.tveu.shiftcrm.api.dto.SellerUpdateRequest;
import ru.tveu.shiftcrm.core.entity.Seller;
import ru.tveu.shiftcrm.core.exception.ErrorCode;
import ru.tveu.shiftcrm.core.exception.ErrorMessage;
import ru.tveu.shiftcrm.core.exception.ServiceException;
import ru.tveu.shiftcrm.core.mapper.SellerMapper;
import ru.tveu.shiftcrm.core.repository.SellerRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class SellerServiceImpl implements SellerService {

    private final SellerRepository sellerRepository;
    private final SellerMapper sellerMapper;

    @Override
    public SellerDTO create(SellerCreateRequest createRequest) {
        log.info("Creating seller with name: {}", createRequest.name());

        Seller seller = sellerMapper.map(createRequest);
        Seller savedSeller = sellerRepository.save(seller);

        log.info("Seller created with ID: {}", savedSeller.getId());
        return sellerMapper.map(savedSeller);
    }

    @Override
    public SellerDTO update(SellerUpdateRequest updateRequest) {
        log.info("Updating seller with id: {}", updateRequest.id());

        Seller seller = sellerRepository.findById(updateRequest.id())
                .orElseThrow(() -> new ServiceException(ErrorCode.OBJECT_NOT_FOUND,
                        ErrorMessage.SELLER_NOT_FOUND + updateRequest.id()));

        boolean hasChanges = false;

        //не позволяю добавлять поля с одними пробелами
        if (updateRequest.contactInfo() != null && !updateRequest.contactInfo().trim().isEmpty()) {
            seller.setContactInfo(updateRequest.contactInfo());
            hasChanges = true;
        }

        if (updateRequest.name() != null && !updateRequest.name().trim().isEmpty()) {
            seller.setName(updateRequest.name());
            hasChanges = true;
        }

        //старасюсь не выполнять дополнитьные sql запросы, если нет изменений
        if (hasChanges) {
            seller = sellerRepository.save(seller);
            log.info("Seller updated successfully with id: {}", seller.getId());
        }

        return sellerMapper.map(seller);
    }

    @Override
    public SellerDTO get(Long id) {
        log.info("Fetching seller with id: {}", id);

        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new ServiceException(ErrorCode.OBJECT_NOT_FOUND, ErrorMessage.SELLER_NOT_FOUND + id));

        return sellerMapper.map(seller);
    }

    @Override
    public Page<SellerDTO> getAll(Pageable pageable) {
        log.info("Fetching all sellers with pagination");
        return sellerMapper.map(sellerRepository.findAll(pageable));
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting seller with id: {}", id);

        if (!sellerRepository.existsById(id)) {
            throw new ServiceException(ErrorCode.OBJECT_NOT_FOUND, ErrorMessage.SELLER_NOT_FOUND + id);
        }

        sellerRepository.deleteById(id);
        log.info("Seller deleted successfully with id: {}", id);
    }
}



