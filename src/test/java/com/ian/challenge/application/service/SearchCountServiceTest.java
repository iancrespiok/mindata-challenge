package com.ian.challenge.application.service;

import com.ian.challenge.Fixture;
import com.ian.challenge.application.services.SearchCountService;
import com.ian.challenge.domain.exception.SearchNotFoundException;
import com.ian.challenge.domain.model.SearchCriteria;
import com.ian.challenge.domain.model.SearchId;
import com.ian.challenge.domain.model.SearchRecord;
import com.ian.challenge.domain.port.in.GetSearchCountUseCase;
import com.ian.challenge.domain.port.out.SearchRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SearchCountServiceTest {
    @Mock
    private SearchRepository searchRepository;

    @Test
    void returnsSearchAndCountWhenFound() {
        SearchCriteria criteria = Fixture.defaultCriteria();
        SearchRecord record = Fixture.recordWithCriteria(criteria);

        when(searchRepository.findById(record.searchId())).thenReturn(Optional.of(record));
        when(searchRepository.countByCriteria(criteria)).thenReturn(100L);

        SearchCountService service = new SearchCountService(searchRepository);
        GetSearchCountUseCase.SearchCountResult result = service.getCount(record.searchId());

        assertAll("search count result when the search has been persisted",
                () -> assertEquals(record, result.search()),
                () -> assertEquals(100L, result.count()),
                () -> verify(searchRepository, times(1)).findById(record.searchId())
        );
    }

    @Test
    void throwsNotFoundForUnknownSearchId() {
        SearchId searchId = SearchId.generate();
        when(searchRepository.findById(searchId)).thenReturn(Optional.empty());

        SearchCountService service = new SearchCountService(searchRepository);

        SearchNotFoundException ex = assertThrows(SearchNotFoundException.class, () -> service.getCount(searchId));

        assertAll(
                () -> assertTrue(ex.getMessage().contains(searchId.value())),
                () -> verify(searchRepository, times(1)).findById(searchId)
        );
    }
}
