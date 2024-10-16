package ru.tveu.shiftcrm.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record SellerCreateRequest(

        @NotNull(message = "ID must not be null for update requests")
        Long id,

        @NotBlank(message = "Name cannot be blank")
        @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
        String name,

        @NotBlank(message = "Contact info cannot be blank")
        @Size(max = 1000, message = "Contact info cannot be longer than 1000 character")
        String contactInfo
) {
}