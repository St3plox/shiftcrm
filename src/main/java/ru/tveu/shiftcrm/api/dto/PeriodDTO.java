package ru.tveu.shiftcrm.api.dto;

import lombok.Builder;

@Builder
public record PeriodDTO(

        String dateStart,

        String dateEnd

) {
}
