package com.ian.challenge.infrastructure.out.kafka.consumer;

import com.ian.challenge.domain.model.SearchRecord;
import com.ian.challenge.infrastructure.adapter.out.kafka.consumer.SearchConsumerMessageMapper;
import com.ian.challenge.infrastructure.adapter.out.kafka.message.SearchAvailabilityMessage;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class SearchConsumerMessageMapperTest {

    private final SearchConsumerMessageMapper mapper = new SearchConsumerMessageMapper();

    @Test
    void mapsWireMessageToDomainRecord() {
        SearchAvailabilityMessage message = new SearchAvailabilityMessage(
                "abc-123", "1234aBc", "2023-12-29", "2023-12-31", List.of(30, 29, 1, 3), Instant.now());

        SearchRecord record = mapper.toDomain(message);

        assertAll("message mapped to domain",
                () -> assertEquals("abc-123", record.searchId().value()),
                () -> assertEquals("1234aBc", record.criteria().hotelId()),
                () -> assertEquals(LocalDate.of(2023, 12, 29), record.criteria().checkIn()),
                () -> assertEquals(LocalDate.of(2023, 12, 31), record.criteria().checkOut()),
                () -> assertIterableEquals(List.of(30, 29, 1, 3), record.criteria().ages()),
                () -> assertEquals(message.registeredAt(), record.registeredAt())
        );
    }
}
