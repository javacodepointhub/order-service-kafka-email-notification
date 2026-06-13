package com.javacodepoint.orderservice.service;

import com.javacodepoint.orderservice.event.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;
    private static final String TOPIC = "order-placed";

    public void publishOrderPlaced(OrderPlacedEvent event) {
        log.info("Publishing order placed event: {}", event.getOrderNumber());
        try {
            kafkaTemplate.send(TOPIC, event.getOrderNumber(), event)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish event to Kafka", ex);
                        } else {
                            log.info("Successfully published to topic {} partition {} offset {}",
                                    result.getRecordMetadata().topic(),
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset());
                        }
                    });
        } catch (Exception e) {
            log.error("Exception while sending to Kafka", e);
        }
    }
}