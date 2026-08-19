package com.ian.challenge.application.port.in;

import com.ian.challenge.domain.model.SearchId;
import com.ian.challenge.domain.model.SearchRecord;

public interface GetSearchCountUseCase {
    SearchCountResult getCount(SearchId searchId);

    record SearchCountResult(SearchRecord search, long count) {
    }
}
