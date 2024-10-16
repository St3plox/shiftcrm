package ru.tveu.shiftcrm.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record SellerUpdateRequest(

        Long id,

        @Size(max = 255, message = "Name must be between 2 and 255 characters")
        String name,

        @JsonProperty("contact_info")
        @Size(max = 1000, message = "Contact info cannot be longer than 1000 character")
        String contactInfo

) {
}
