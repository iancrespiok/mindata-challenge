package com.ian.challenge.infrastructure.adapter.out.kafka.producer;

import com.ian.challenge.domain.model.SearchRecord;
import com.ian.challenge.domain.port.out.SearchEventPublisher;
import com.ian.challenge.infrastructure.adapter.out.kafka.message.SearchAvailabilityMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaSearchEventPublisher implements SearchEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(KafkaSearchEventPublisher.class);
    public static final String TOPIC = "hotel_availability_searches";

    private final KafkaTemplate<String, SearchAvailabilityMessage> kafkaTemplate;
    private final SearchProducerMessageMapper mapper;

    public KafkaSearchEventPublisher(KafkaTemplate<String, SearchAvailabilityMessage> kafkaTemplate, SearchProducerMessageMapper mapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.mapper = mapper;
    }

    @Override
    public void publish(SearchRecord searchRecord) {
        SearchAvailabilityMessage message = mapper.toMessage(searchRecord);
        kafkaTemplate.send(TOPIC, searchRecord.searchId().value(), message)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Error publishing search {} on Kafka", searchRecord.searchId().value(), ex);
                    } else {
                        log.debug("Search {} published on {}", searchRecord.searchId().value(), TOPIC);
                    }
                });
    }
}
