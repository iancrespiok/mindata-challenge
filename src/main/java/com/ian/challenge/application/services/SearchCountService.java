package com.ian.challenge.application.services;

import com.ian.challenge.domain.exception.SearchNotFoundException;
import com.ian.challenge.domain.model.SearchId;
import com.ian.challenge.domain.model.SearchRecord;
import com.ian.challenge.application.port.in.GetSearchCountUseCase;
import com.ian.challenge.domain.port.out.SearchRepository;

public class SearchCountService implements GetSearchCountUseCase {
    private final SearchRepository searchRepository;

    public SearchCountService(SearchRepository searchRepository) {
        this.searchRepository = searchRepository;
    }

    @Override
    public SearchCountResult getCount(SearchId searchId) {
        SearchRecord searchRecord = searchRepository.findById(searchId)
                .orElseThrow(() -> new SearchNotFoundException(searchId.value()));
        long count = searchRepository.countByCriteria(searchRecord.criteria());
        return new SearchCountResult(searchRecord, count);
    }
}
