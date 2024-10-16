package ru.tveu.shiftcrm.core.mapper;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import ru.tveu.shiftcrm.api.dto.SellerCreateRequest;
import ru.tveu.shiftcrm.api.dto.SellerDTO;
import ru.tveu.shiftcrm.core.entity.Seller;

@Component
public class SellerMapper {

    public Seller map(SellerCreateRequest request) {

        return Seller.builder()
                .name(request.name())
                .contactInfo(request.contactInfo())
                .build();
    }

    public SellerDTO map(Seller seller) {

        return SellerDTO.builder()
                .id(seller.getId())
                .name(seller.getName())
                .contactInfo(seller.getContactInfo())
                .registrationDate(seller.getRegistrationDate().toString())
                .build();
    }

    public Page<SellerDTO> map(Page<Seller> sellers) {
        return sellers.map(this::map);
    }
}
