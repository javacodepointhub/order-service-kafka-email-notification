package com.javacodepoint.orderservice.consumer;

import com.javacodepoint.orderservice.event.OrderPlacedEvent;
import com.javacodepoint.orderservice.service.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderConsumer {

    @Autowired
    private final EmailService emailService;

    @RetryableTopic(
            attempts = "3",
            dltStrategy = DltStrategy.FAIL_ON_ERROR,
            autoCreateTopics = "true"
    )
    @KafkaListener(topics = "order-placed", groupId = "order-group")
    public void consume(OrderPlacedEvent event) throws MessagingException {
        log.info("Consuming order placed event: {}", event.getOrderNumber());
        emailService.sendOrderConfirmation(event);
    }

    @DltHandler
    public void handleDlt(OrderPlacedEvent event) {
        log.error("Order moved to Dead Letter Topic. Order: {} | Email: {}",
                event.getOrderNumber(), event.getCustomerEmail());
        // TODO: You can save failed orders to DB for manual retry later
    }
}