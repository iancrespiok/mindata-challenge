package com.ian.challenge.infrastructure.adapter.out.kafka.producer;

import com.ian.challenge.Fixture;
import com.ian.challenge.domain.model.SearchCriteria;
import com.ian.challenge.domain.model.SearchRecord;
import com.ian.challenge.infrastructure.adapter.out.kafka.message.SearchAvailabilityMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaSearchEventPublisherTest {

    @Mock
    private KafkaTemplate<String, SearchAvailabilityMessage> kafkaTemplate;

    @Test
    void publishesMessageWithSearchIdAsKey() {
        SearchProducerMessageMapper mapper = new SearchProducerMessageMapper();
        KafkaSearchEventPublisher publisher = new KafkaSearchEventPublisher(kafkaTemplate, mapper);

        SearchCriteria criteria = Fixture.defaultCriteria();
        SearchRecord searchRecord = Fixture.recordWithCriteria(criteria);

        CompletableFuture<SendResult<String, SearchAvailabilityMessage>> future = new CompletableFuture<>();
        future.complete(null);
        when(kafkaTemplate.send(eq(KafkaSearchEventPublisher.TOPIC), eq(searchRecord.searchId().value()), any()))
                .thenReturn(future);

        publisher.publish(searchRecord);

        verify(kafkaTemplate).send(eq(KafkaSearchEventPublisher.TOPIC), eq(searchRecord.searchId().value()), any());
    }

    @Test
    void logsErrorWhenPublishFails() {
        SearchProducerMessageMapper mapper = new SearchProducerMessageMapper();
        KafkaSearchEventPublisher publisher = new KafkaSearchEventPublisher(kafkaTemplate, mapper);

        SearchCriteria criteria = Fixture.shortWindowCriteria(List.of(1));
        SearchRecord searchRecord = Fixture.recordWithCriteria(criteria);

        CompletableFuture<SendResult<String, SearchAvailabilityMessage>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("broker down"));
        when(kafkaTemplate.send(eq(KafkaSearchEventPublisher.TOPIC), eq(searchRecord.searchId().value()), any()))
                .thenReturn(future);

        publisher.publish(searchRecord);

        verify(kafkaTemplate).send(eq(KafkaSearchEventPublisher.TOPIC), eq(searchRecord.searchId().value()), any());
    }
}
