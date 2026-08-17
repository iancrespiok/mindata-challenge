package com.ian.challenge.domain.model;

import java.time.Instant;
import java.util.Objects;

public record SearchRecord(SearchId searchId, SearchCriteria criteria, Instant registeredAt) {
    public SearchRecord {
        Objects.requireNonNull(searchId, "searchId cannot be null");
        Objects.requireNonNull(criteria, "criteria cannot be null");
        Objects.requireNonNull(registeredAt, "registeredAt cannot be null");
    }

    public static SearchRecord newSearch(SearchCriteria criteria) {
        return new SearchRecord(SearchId.generate(), criteria, Instant.now());

    }
}
