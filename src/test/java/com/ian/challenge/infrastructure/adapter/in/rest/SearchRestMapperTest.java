package com.ian.challenge.infrastructure.adapter.in.rest;

import com.ian.challenge.Fixture;
import com.ian.challenge.domain.model.SearchCriteria;
import com.ian.challenge.domain.model.SearchId;
import com.ian.challenge.domain.model.SearchRecord;
import com.ian.challenge.infrastructure.adapter.in.rest.SearchRestMapper;
import com.ian.challenge.infrastructure.adapter.in.rest.dto.SearchCountResponseDTO;
import com.ian.challenge.infrastructure.adapter.in.rest.dto.SearchRequestDTO;
import com.ian.challenge.infrastructure.adapter.in.rest.dto.SearchResponseDTO;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class SearchRestMapperTest {

    private final SearchRestMapper mapper = new SearchRestMapper();

    @Test
    void mapsRequestDtoToDomainParsingDates() {
        SearchRequestDTO dto = Fixture.defaultRequestDto();

        SearchCriteria criteria = mapper.toDomain(dto);

        assertAll("el DTO se traduce fielmente al modelo de dominio",
                () -> assertEquals("1234aBc", criteria.hotelId()),
                () -> assertEquals(LocalDate.of(2023, 12, 29), criteria.checkIn()),
                () -> assertEquals(LocalDate.of(2023, 12, 31), criteria.checkOut()),
                () -> assertIterableEquals(List.of(30, 29, 1, 3), criteria.ages())
        );
    }

    @Test
    void mapsSearchIdToResponse() {
        SearchId id = SearchId.generate();
        SearchResponseDTO response = mapper.toResponse(id);
        assertEquals(id.value(), response.searchId());
    }

    @Test
    void mapsRecordAndCountToCountResponse() {
        SearchCriteria criteria = Fixture.criteriaWithAges(List.of(3, 29, 30, 1));
        SearchRecord record = Fixture.recordWithCriteria(criteria);

        SearchCountResponseDTO response = mapper.toCountResponse(record, 100L);

        assertAll("count response reflects register and total count",
                () -> assertEquals(record.searchId().value(), response.searchId()),
                () -> assertEquals(100L, response.count()),
                () -> assertEquals("1234aBc", response.search().hotelId()),
                () -> assertEquals("29/12/2023", response.search().checkIn()),
                () -> assertEquals("31/12/2023", response.search().checkOut()),
                () -> assertIterableEquals(List.of(3, 29, 30, 1), response.search().ages())
        );
    }
}