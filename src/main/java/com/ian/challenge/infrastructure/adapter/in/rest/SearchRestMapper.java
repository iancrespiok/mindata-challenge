package com.ian.challenge.infrastructure.adapter.in.rest;

import com.ian.challenge.domain.model.SearchCriteria;
import com.ian.challenge.domain.model.SearchId;
import com.ian.challenge.domain.model.SearchRecord;
import com.ian.challenge.infrastructure.adapter.in.rest.dto.SearchCountResponseDTO;
import com.ian.challenge.infrastructure.adapter.in.rest.dto.SearchRequestDTO;
import com.ian.challenge.infrastructure.adapter.in.rest.dto.SearchResponseDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class SearchRestMapper {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public SearchCriteria toDomain(SearchRequestDTO dto) {
        LocalDate checkIn = LocalDate.parse(dto.checkIn(), DATE_FORMAT);
        LocalDate checkOut = LocalDate.parse(dto.checkOut(), DATE_FORMAT);
        return new SearchCriteria(dto.hotelId(), checkIn, checkOut, dto.ages());
    }

    public SearchResponseDTO toResponse(SearchId searchId) {
        return new SearchResponseDTO(searchId.value());
    }

    public SearchCountResponseDTO toCountResponse(SearchRecord searchRecord, long count) {
        SearchCriteria criteria = searchRecord.criteria();
        SearchCountResponseDTO.SearchDto searchDto = new SearchCountResponseDTO.SearchDto(
                criteria.hotelId(),
                criteria.checkIn().format(DATE_FORMAT),
                criteria.checkOut().format(DATE_FORMAT),
                criteria.ages()
        );
        return new SearchCountResponseDTO(searchRecord.searchId().value(), searchDto, count);
    }
}
