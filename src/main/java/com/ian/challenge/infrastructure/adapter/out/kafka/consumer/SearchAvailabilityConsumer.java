package com.ian.challenge.infrastructure.adapter.out.kafka.consumer;

import com.ian.challenge.domain.model.SearchRecord;
import com.ian.challenge.domain.port.out.SearchRepository;
import com.ian.challenge.infrastructure.adapter.out.kafka.message.SearchAvailabilityMessage;
import com.ian.challenge.infrastructure.adapter.out.kafka.producer.KafkaSearchEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

@Component
public class SearchAvailabilityConsumer {
    private static final Logger log = LoggerFactory.getLogger(SearchAvailabilityConsumer.class);

    private final SearchRepository searchRepository;
    private final SearchConsumerMessageMapper mapper;
    private final ExecutorService virtualThreadExecutor;

    public SearchAvailabilityConsumer(SearchRepository searchRepository, SearchConsumerMessageMapper mapper, ExecutorService virtualThreadExecutor) {
        this.searchRepository = searchRepository;
        this.mapper = mapper;
        this.virtualThreadExecutor = virtualThreadExecutor;
    }

    @KafkaListener(topics = KafkaSearchEventPublisher.TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void onMessage(SearchAvailabilityMessage message) {
        SearchRecord searchRecord = mapper.toDomain(message);
        // El guardado se despacha a un hilo virtual: cada invocacion recibe su propio hilo
        // liviano, sin estado compartido mutable entre invocaciones (thread-safe por diseno).
        virtualThreadExecutor.submit(() -> persist(searchRecord));
    }

    private void persist(SearchRecord searchRecord) {
        try {
            searchRepository.save(searchRecord);
            log.debug("Search {} persisted by thread {}", searchRecord.searchId().value(), Thread.currentThread());
        } catch (Exception e) {
            log.error("Error persisting search {}", searchRecord.searchId().value(), e);
        }
    }

}
