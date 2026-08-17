package com.ian.challenge.application.service;

import com.ian.challenge.application.services.SearchRegistrationService;
import com.ian.challenge.domain.model.SearchCriteria;
import com.ian.challenge.domain.model.SearchId;
import com.ian.challenge.domain.model.SearchRecord;
import com.ian.challenge.domain.port.out.SearchEventPublisher;
import com.ian.challenge.Fixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class SearchRegistrationServiceTest {

    @Mock
    private SearchEventPublisher eventPublisher;

    @Test
    void registersSearchAndPublishesEvent() {
        SearchRegistrationService service = new SearchRegistrationService(eventPublisher);
        SearchCriteria criteria = Fixture.defaultCriteria();

        SearchId searchId = service.registerSearch(criteria);

        ArgumentCaptor<SearchRecord> captor = ArgumentCaptor.forClass(SearchRecord.class);
        verify(eventPublisher).publish(captor.capture());
        assertAll("search registered and event published with the same data",
                () -> assertThat(searchId).isNotNull(),
                () -> assertEquals(searchId, captor.getValue().searchId()),
                () -> assertEquals(criteria, captor.getValue().criteria())
        );
    }

    @Test
    void twoIdenticalSearchesGetDifferentUniqueIds() {
        SearchRegistrationService service = new SearchRegistrationService(eventPublisher);
        SearchCriteria criteria = Fixture.defaultCriteria();

        SearchId first = service.registerSearch(criteria);
        SearchId second = service.registerSearch(criteria);

        assertNotEquals(first, second);
    }
/*
    @Test
    void concurrentRegistrationsAlwaysProduceUniqueIds() throws Exception {
        SearchRegistrationService service = new SearchRegistrationService(eventPublisher);
        SearchCriteria criteria = Fixture.defaultCriteria();

        int concurrentRequests = 100;
        ExecutorService executor = Executors.newFixedThreadPool(16);
        try {
            List<Callable<SearchId>> tasks = IntStream.range(0, concurrentRequests)
                    .<Callable<SearchId>>mapToObj(i -> () -> service.registerSearch(criteria))
                    .toList();

            List<Future<SearchId>> futures = executor.invokeAll(tasks);
            Set<SearchId> uniqueIds = ConcurrentHashMap.newKeySet();
            for (Future<SearchId> future : futures) {
                uniqueIds.add(future.get());
            }

            assertEquals(concurrentRequests, uniqueIds.size());
            verify(eventPublisher, times(concurrentRequests)).publish(org.mockito.ArgumentMatchers.any());
        } finally {
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }
    }*/
}
