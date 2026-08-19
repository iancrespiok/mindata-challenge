package com.ian.challenge.infrastructure.adapter.in.rest;

import com.ian.challenge.Fixture;
import com.ian.challenge.domain.model.SearchCriteria;
import com.ian.challenge.domain.model.SearchId;
import com.ian.challenge.domain.model.SearchRecord;
import com.ian.challenge.infrastructure.adapter.in.rest.dto.SearchCountResponseDTO;
import com.ian.challenge.infrastructure.adapter.in.rest.dto.SearchRequestDTO;
import com.ian.challenge.infrastructure.adapter.in.rest.dto.SearchResponseDTO;
import org.junit.jupiter.api.Test;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class SearchRestMapperTest {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final SearchRestMapper mapper = new SearchRestMapper();

    @Test
    void mapsRequestDtoToDomainParsingDates() {
        SearchRequestDTO dto = Fixture.defaultRequestDto();

        SearchCriteria criteria = mapper.toDomain(dto);

        assertAll("DTO maps correctly to domain model",
                () -> assertEquals("1234aBc", criteria.hotelId()),
                () -> assertEquals(Fixture.DEFAULT_CHECK_IN, criteria.checkIn()),
                () -> assertEquals(Fixture.DEFAULT_CHECK_OUT, criteria.checkOut()),
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
                () -> assertEquals(Fixture.DEFAULT_CHECK_IN.format(DATE_FORMAT), response.search().checkIn()),
                () -> assertEquals(Fixture.DEFAULT_CHECK_OUT.format(DATE_FORMAT), response.search().checkOut()),
                () -> assertIterableEquals(List.of(3, 29, 30, 1), response.search().ages())
        );
    }

}