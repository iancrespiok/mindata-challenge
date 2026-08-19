package com.ian.challenge.application.services;

import com.ian.challenge.domain.model.SearchCriteria;
import com.ian.challenge.domain.model.SearchId;
import com.ian.challenge.domain.model.SearchRecord;
import com.ian.challenge.application.port.in.RegisterSearchUseCase;
import com.ian.challenge.domain.port.out.SearchEventPublisher;

public class SearchRegistrationService implements RegisterSearchUseCase {
    private final SearchEventPublisher eventPublisher;

    public SearchRegistrationService(SearchEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public SearchId registerSearch(SearchCriteria criteria) {
        SearchRecord searchRecord = SearchRecord.newSearch(criteria);
        eventPublisher.publish(searchRecord);
        return searchRecord.searchId();
    }
}
