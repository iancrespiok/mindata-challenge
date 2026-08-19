package com.ian.challenge.infrastructure.adapter.out.kafka.consumer;

import com.ian.challenge.domain.model.SearchRecord;
import com.ian.challenge.infrastructure.adapter.out.kafka.message.SearchAvailabilityMessage;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class SearchConsumerMessageMapperTest {

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final LocalDate CHECK_IN = LocalDate.now().plusDays(30);
    private static final LocalDate CHECK_OUT = LocalDate.now().plusDays(32);

    private final SearchConsumerMessageMapper mapper = new SearchConsumerMessageMapper();

    @Test
    void mapsWireMessageToDomainRecord() {
        SearchAvailabilityMessage message = new SearchAvailabilityMessage(
                "abc-123", "1234aBc", CHECK_IN.format(ISO), CHECK_OUT.format(ISO), List.of(30, 29, 1, 3), Instant.now());

        SearchRecord record = mapper.toDomain(message);

        assertAll("registro de dominio a partir del mensaje recibido",
                () -> assertEquals("abc-123", record.searchId().value()),
                () -> assertEquals("1234aBc", record.criteria().hotelId()),
                () -> assertEquals(CHECK_IN, record.criteria().checkIn()),
                () -> assertEquals(CHECK_OUT, record.criteria().checkOut()),
                () -> assertIterableEquals(List.of(30, 29, 1, 3), record.criteria().ages()),
                () -> assertEquals(message.registeredAt(), record.registeredAt())
        );
    }
}
