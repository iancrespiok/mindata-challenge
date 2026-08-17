package com.ian.challenge.infrastructure.adapter.out.kafka.consumer;

import com.ian.challenge.domain.model.SearchRecord;
import com.ian.challenge.domain.port.out.SearchRepository;
import com.ian.challenge.infrastructure.adapter.out.kafka.message.SearchAvailabilityMessage;
import com.ian.challenge.infrastructure.adapter.out.kafka.producer.KafkaSearchEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.retrytopic.RetryTopicHeaders;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
public class SearchAvailabilityConsumer {

    private static final Logger log = LoggerFactory.getLogger(SearchAvailabilityConsumer.class);

    private final SearchRepository searchRepository;
    private final SearchConsumerMessageMapper mapper;

    public SearchAvailabilityConsumer(SearchRepository searchRepository,
                                      SearchConsumerMessageMapper mapper) {
        this.searchRepository = searchRepository;
        this.mapper = mapper;
    }

    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(delay = 1000, multiplier = 2.0, maxDelay = 10000),
            dltStrategy = DltStrategy.FAIL_ON_ERROR,
            autoCreateTopics = "true")
    @KafkaListener(topics = KafkaSearchEventPublisher.TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void onMessage(SearchAvailabilityMessage message) {
        SearchRecord searchRecord = mapper.toDomain(message);
        searchRepository.save(searchRecord);
        log.debug("Search {} persisted by {}", searchRecord.searchId().value(), Thread.currentThread());
    }

    @DltHandler
    public void handleDlt(SearchAvailabilityMessage message,
                          @Header(KafkaHeaders.EXCEPTION_MESSAGE) String exceptionMessage,
                          @Header(RetryTopicHeaders.DEFAULT_HEADER_ATTEMPTS) int attempts) {
        log.error("Search {} exhausted after {} attempts and crashed to the DLT. Cause: {}", message.searchId(), attempts, exceptionMessage);
    }
}