package com.ian.challenge.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.*;

import java.util.List;

public record SearchRequestDTO (
        @NotBlank(message = "hotelId is mandatory")
        String hotelId,

        @NotNull(message = "checkIn is mandatory")
        @Pattern(regexp = "\\d{2}/\\d{2}/\\d{4}", message = "checkIn must be formatted as dd/MM/yyyy")
        String checkIn,

        @NotNull(message = "checkOut is mandatory")
        @Pattern(regexp = "\\d{2}/\\d{2}/\\d{4}", message = "checkOut must be formatted as dd/MM/yyyy")
        String checkOut,

        @NotEmpty(message = "ages can not be empty")
        List<@NotNull @PositiveOrZero(message = "each age must be greater than 0") Integer> ages
){
}
