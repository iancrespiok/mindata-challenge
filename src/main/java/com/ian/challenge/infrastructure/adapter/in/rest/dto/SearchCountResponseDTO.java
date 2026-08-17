package com.ian.challenge.infrastructure.adapter.in.rest.dto;

import java.util.List;

public record SearchCountResponseDTO(String searchId, SearchDto search, long count) {
    public record SearchDto(String hotelId, String checkIn, String checkOut, List<Integer> ages) {
    }
}
