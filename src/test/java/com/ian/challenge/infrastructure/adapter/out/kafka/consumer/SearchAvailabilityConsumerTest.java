package com.ian.challenge.infrastructure.adapter.out.kafka.consumer;

import com.ian.challenge.domain.model.SearchRecord;
import com.ian.challenge.domain.port.out.SearchRepository;
import com.ian.challenge.infrastructure.adapter.out.kafka.consumer.SearchAvailabilityConsumer;
import com.ian.challenge.infrastructure.adapter.out.kafka.consumer.SearchConsumerMessageMapper;
import com.ian.challenge.infrastructure.adapter.out.kafka.message.SearchAvailabilityMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SearchAvailabilityConsumerTest {

    @Mock
    private SearchRepository searchRepository;

    @Test
    void persistsMessageSynchronously() {
        SearchConsumerMessageMapper mapper = new SearchConsumerMessageMapper();
        SearchAvailabilityConsumer consumer = new SearchAvailabilityConsumer(searchRepository, mapper);

        SearchAvailabilityMessage message = new SearchAvailabilityMessage(
                "abc-123", "1234aBc", "2023-12-29", "2023-12-31", List.of(30, 29, 1, 3), Instant.now());

        consumer.onMessage(message);

        verify(searchRepository).save(any(SearchRecord.class));
    }

    @Test
    void propagatesExceptionSoRetryableTopicCanCatchIt() {
        SearchConsumerMessageMapper mapper = new SearchConsumerMessageMapper();
        SearchAvailabilityConsumer consumer = new SearchAvailabilityConsumer(searchRepository, mapper);

        doThrow(new RuntimeException("db down")).when(searchRepository).save(any());

        SearchAvailabilityMessage message = new SearchAvailabilityMessage(
                "abc-123", "hotel", "2023-12-29", "2023-12-31", List.of(1), Instant.now());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> consumer.onMessage(message));
        assertEquals("db down", thrown.getMessage());
    }
}