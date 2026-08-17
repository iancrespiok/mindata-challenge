package com.ian.challenge.infrastructure.out.kafka.consumer;

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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SearchAvailabilityConsumerTest {

    @Mock
    private SearchRepository searchRepository;

    @Test
    void dispatchesMessagePersistenceToVirtualThreadExecutor() throws InterruptedException {
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        SearchConsumerMessageMapper mapper = new SearchConsumerMessageMapper();
        SearchAvailabilityConsumer consumer = new SearchAvailabilityConsumer(searchRepository, mapper, executor);

        SearchAvailabilityMessage message = new SearchAvailabilityMessage(
                "abc-123", "1234aBc", "2023-12-29", "2023-12-31", List.of(30, 29, 1, 3), Instant.now());

        consumer.onMessage(message);

        verify(searchRepository, timeout(2000)).save(any(SearchRecord.class));
        executor.shutdown();
        assertTrue(executor.awaitTermination(2, TimeUnit.SECONDS));
    }

    @Test
    void swallowsAndLogsPersistenceFailures() throws InterruptedException {
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        SearchConsumerMessageMapper mapper = new SearchConsumerMessageMapper();
        SearchAvailabilityConsumer consumer = new SearchAvailabilityConsumer(searchRepository, mapper, executor);

        doThrow(new RuntimeException("db down")).when(searchRepository).save(any());

        SearchAvailabilityMessage message = new SearchAvailabilityMessage(
                "abc-123", "hotel", "2023-12-29", "2023-12-31", List.of(1), Instant.now());

        CountDownLatch latch = new CountDownLatch(1);
        consumer.onMessage(message);
        executor.submit(latch::countDown);

        assertAll(
                () -> assertTrue(latch.await(2, TimeUnit.SECONDS)),
                () -> verify(searchRepository, timeout(2000)).save(any())
        );
        executor.shutdown();
    }
}

