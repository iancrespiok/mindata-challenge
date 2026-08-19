package com.ian.challenge.application.service;

import com.ian.challenge.Fixture;
import com.ian.challenge.application.services.SearchRegistrationService;
import com.ian.challenge.domain.model.SearchCriteria;
import com.ian.challenge.domain.model.SearchId;
import com.ian.challenge.domain.model.SearchRecord;
import com.ian.challenge.domain.port.out.SearchEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.verify;

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
}
