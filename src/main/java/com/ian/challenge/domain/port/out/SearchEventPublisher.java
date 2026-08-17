package com.ian.challenge.domain.port.out;

import com.ian.challenge.domain.model.SearchRecord;

public interface SearchEventPublisher {
    void publish(SearchRecord searchRecord);
}
