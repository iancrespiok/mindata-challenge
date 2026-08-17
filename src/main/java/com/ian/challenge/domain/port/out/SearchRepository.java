package com.ian.challenge.domain.port.out;

import com.ian.challenge.domain.model.SearchCriteria;
import com.ian.challenge.domain.model.SearchId;
import com.ian.challenge.domain.model.SearchRecord;

import java.util.Optional;

public interface SearchRepository {
    void save(SearchRecord searchRecord);

    Optional<SearchRecord> findById(SearchId searchId);

    long countByCriteria(SearchCriteria criteria);

}
