package com.ian.challenge.infrastructure.config;

import com.ian.challenge.infrastructure.adapter.out.kafka.producer.KafkaSearchEventPublisher;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Bean
    public NewTopic hotelAvailabilitySearchesTopic() {
        return TopicBuilder.name(KafkaSearchEventPublisher.TOPIC)
                .partitions(2)
                .replicas(1)
                .build();
    }
}
