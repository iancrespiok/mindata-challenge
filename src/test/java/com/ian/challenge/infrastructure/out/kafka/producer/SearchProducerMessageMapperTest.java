package com.ian.challenge.infrastructure.out.kafka.producer;

import com.ian.challenge.Fixture;
import com.ian.challenge.domain.model.SearchCriteria;
import com.ian.challenge.domain.model.SearchRecord;
import com.ian.challenge.infrastructure.adapter.out.kafka.message.SearchAvailabilityMessage;
import com.ian.challenge.infrastructure.adapter.out.kafka.producer.SearchProducerMessageMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class SearchProducerMessageMapperTest {

    private final SearchProducerMessageMapper mapper = new SearchProducerMessageMapper();

    @Test
    void mapsDomainRecordToWireMessage() {
        SearchCriteria criteria = Fixture.defaultCriteria();
        SearchRecord record = Fixture.recordWithCriteria(criteria);

        SearchAvailabilityMessage message = mapper.toMessage(record);

        assertAll("mensaje producido a partir del registro de dominio",
                () -> assertEquals(record.searchId().value(), message.searchId()),
                () -> assertEquals("1234aBc", message.hotelId()),
                () -> assertEquals("2023-12-29", message.checkIn()),
                () -> assertEquals("2023-12-31", message.checkOut()),
                () -> assertIterableEquals(List.of(30, 29, 1, 3), message.ages()),
                () -> assertEquals(record.registeredAt(), message.registeredAt())
        );
    }
}
