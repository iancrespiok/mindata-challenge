package com.ian.challenge.application.port.in;

import com.ian.challenge.domain.model.SearchCriteria;
import com.ian.challenge.domain.model.SearchId;

public interface RegisterSearchUseCase {
    SearchId registerSearch(SearchCriteria criteria);
}
