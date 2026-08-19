package com.ian.challenge;

import com.ian.challenge.domain.model.SearchCriteria;
import com.ian.challenge.domain.model.SearchId;
import com.ian.challenge.domain.model.SearchRecord;
import com.ian.challenge.infrastructure.adapter.in.rest.dto.SearchRequestDTO;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Fixture {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static final String DEFAULT_HOTEL_ID = "1234aBc";
    public static final LocalDate DEFAULT_CHECK_IN = LocalDate.now().plusDays(30);
    public static final LocalDate DEFAULT_CHECK_OUT = LocalDate.now().plusDays(32);
    public static final List<Integer> DEFAULT_AGES = List.of(30, 29, 1, 3);

    private Fixture() {
    }

    public static SearchCriteria defaultCriteria() {
        return new SearchCriteria(DEFAULT_HOTEL_ID, DEFAULT_CHECK_IN, DEFAULT_CHECK_OUT, DEFAULT_AGES);
    }

    public static SearchCriteria criteriaWithAges(List<Integer> ages) {
        return new SearchCriteria(DEFAULT_HOTEL_ID, DEFAULT_CHECK_IN, DEFAULT_CHECK_OUT, ages);
    }

    public static SearchCriteria criteriaWithHotelId(String hotelId) {
        return new SearchCriteria(hotelId, LocalDate.now(), LocalDate.now().plusDays(1), List.of(1));
    }

    public static SearchCriteria shortWindowCriteria(List<Integer> ages) {
        return new SearchCriteria("hotel", LocalDate.now(), LocalDate.now().plusDays(1), ages);
    }

    public static SearchRecord defaultRecord() {
        return new SearchRecord(SearchId.generate(), defaultCriteria(), Instant.now());
    }

    public static SearchRecord recordWithCriteria(SearchCriteria criteria) {
        return new SearchRecord(SearchId.generate(), criteria, Instant.now());
    }

    public static SearchRecord recordWithId(SearchId id, SearchCriteria criteria) {
        return new SearchRecord(id, criteria, Instant.now());
    }

    public static SearchRequestDTO defaultRequestDto() {
        return new SearchRequestDTO(DEFAULT_HOTEL_ID, DEFAULT_CHECK_IN.format(DATE_FORMAT),
                DEFAULT_CHECK_OUT.format(DATE_FORMAT), DEFAULT_AGES);
    }

    public static final String DEFAULT_REQUEST_JSON = """
            {
              "hotelId": "%s",
              "checkIn": "%s",
              "checkOut": "%s",
              "ages": [30, 29, 1, 3]
            }
            """.formatted(DEFAULT_HOTEL_ID, DEFAULT_CHECK_IN.format(DATE_FORMAT), DEFAULT_CHECK_OUT.format(DATE_FORMAT));
}

