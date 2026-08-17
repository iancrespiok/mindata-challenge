package com.ian.challenge.infrastructure.adapter.out.kafka.consumer;

import com.ian.challenge.domain.model.SearchCriteria;
import com.ian.challenge.domain.model.SearchId;
import com.ian.challenge.domain.model.SearchRecord;
import com.ian.challenge.infrastructure.adapter.out.kafka.message.SearchAvailabilityMessage;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class SearchConsumerMessageMapper {

    public SearchRecord toDomain(SearchAvailabilityMessage message) {
        SearchCriteria criteria = new SearchCriteria(
                message.hotelId(),
                LocalDate.parse(message.checkIn()),
                LocalDate.parse(message.checkOut()),
                message.ages()
        );
        return new SearchRecord(new SearchId(message.searchId()), criteria, message.registeredAt());
    }
}
