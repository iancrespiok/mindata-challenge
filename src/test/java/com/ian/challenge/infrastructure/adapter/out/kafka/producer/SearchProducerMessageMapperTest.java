package com.ian.challenge.infrastructure.adapter.out.kafka.producer;

import com.ian.challenge.Fixture;
import com.ian.challenge.domain.model.SearchCriteria;
import com.ian.challenge.domain.model.SearchRecord;
import com.ian.challenge.infrastructure.adapter.out.kafka.message.SearchAvailabilityMessage;
import org.junit.jupiter.api.Test;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class SearchProducerMessageMapperTest {

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE;

    private final SearchProducerMessageMapper mapper = new SearchProducerMessageMapper();

    @Test
    void mapsDomainRecordToWireMessage() {
        SearchCriteria criteria = Fixture.defaultCriteria();
        SearchRecord record = Fixture.recordWithCriteria(criteria);

        SearchAvailabilityMessage message = mapper.toMessage(record);

        assertAll("mensaje producido a partir del registro de dominio",
                () -> assertEquals(record.searchId().value(), message.searchId()),
                () -> assertEquals("1234aBc", message.hotelId()),
                () -> assertEquals(Fixture.DEFAULT_CHECK_IN.format(ISO), message.checkIn()),
                () -> assertEquals(Fixture.DEFAULT_CHECK_OUT.format(ISO), message.checkOut()),
                () -> assertIterableEquals(List.of(30, 29, 1, 3), message.ages()),
                () -> assertEquals(record.registeredAt(), message.registeredAt())
        );
    }
}
