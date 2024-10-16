package ru.tveu.shiftcrm.api.dto;

import lombok.Builder;

@Builder
public record SellerDTO(

        Long id,
        String name,

        String contactInfo,

        String registrationDate
) {

}

