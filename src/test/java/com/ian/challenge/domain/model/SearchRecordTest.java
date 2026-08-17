package com.ian.challenge.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SearchRecordTest {

    @Test
    void newSearchAssignsIdAndTimestamp() {
        SearchCriteria criteria = new SearchCriteria("hotel", LocalDate.now(), LocalDate.now().plusDays(2), List.of(30));
        SearchRecord record = SearchRecord.newSearch(criteria);

        assertAll("a new SearchRecord is created",
                () -> assertFalse(record.searchId().value().isBlank()),
                () -> assertEquals(criteria, record.criteria()),
                () -> assertNotNull(record.registeredAt())
        );
    }

    @Test
    void rejectsNullArguments() {
        SearchCriteria criteria = new SearchCriteria("hotel", LocalDate.now(), LocalDate.now().plusDays(1), List.of(1));
        Instant now = Instant.now();
        assertAll("no component of a SearchRecord can be null",
                () -> assertThrows(NullPointerException.class, () -> new SearchRecord(null, criteria, now)),
                () -> assertThrows(NullPointerException.class, () -> new SearchRecord(SearchId.generate(), null, now)),
                () -> assertThrows(NullPointerException.class, () -> new SearchRecord(SearchId.generate(), criteria, null))
        );
    }}
