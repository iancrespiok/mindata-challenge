package com.ian.challenge.infrastructure.adapter.out.kafka.producer;

import com.ian.challenge.domain.model.SearchCriteria;
import com.ian.challenge.domain.model.SearchRecord;
import com.ian.challenge.infrastructure.adapter.out.kafka.message.SearchAvailabilityMessage;
import org.springframework.stereotype.Component;

@Component
public class SearchProducerMessageMapper {
    public SearchAvailabilityMessage toMessage(SearchRecord searchRecord) {
        SearchCriteria criteria = searchRecord.criteria();
        return new SearchAvailabilityMessage(
                searchRecord.searchId().value(),
                criteria.hotelId(),
                criteria.checkIn().toString(),
                criteria.checkOut().toString(),
                criteria.ages(),
                searchRecord.registeredAt()
        );
    }
}
