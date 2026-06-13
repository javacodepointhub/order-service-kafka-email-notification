package com.javacodepoint.orderservice.service;

import com.javacodepoint.orderservice.dto.OrderRequest;
import com.javacodepoint.orderservice.event.OrderPlacedEvent;
import com.javacodepoint.orderservice.model.Order;
import com.javacodepoint.orderservice.model.OrderItem;
import com.javacodepoint.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final KafkaProducerService kafkaProducerService;

    @Transactional
    public Order placeOrder(OrderRequest request) {
        log.info("Placing new order for customer: {}", request.getCustomerName());

        Order order = new Order();
        order.setCustomerName(request.getCustomerName());
        order.setCustomerEmail(request.getCustomerEmail());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setShippingAddress(request.getShippingAddress());

        var orderItems = request.getItems().stream()
                .map(item -> {
                    OrderItem oi = new OrderItem();
                    oi.setProductName(item.getProductName());
                    oi.setQuantity(item.getQuantity());
                    oi.setPrice(BigDecimal.valueOf(item.getPrice()));
                    return oi;
                }).collect(Collectors.toList());

        order.setItems(orderItems);
        order.setSubtotal(BigDecimal.valueOf(99.99)); // Calculate properly in real app
        order.setShippingCost(BigDecimal.valueOf(9.99));
        order.setTax(BigDecimal.valueOf(8.00));
        order.setTotalAmount(BigDecimal.valueOf(117.98));

        Order savedOrder = orderRepository.save(order);
        log.info("Order saved with ID: {}", savedOrder.getId());

        // Publish event to Kafka
        OrderPlacedEvent event = new OrderPlacedEvent(
                savedOrder.getId(),
                savedOrder.getOrderNumber(),
                savedOrder.getCustomerName(),
                savedOrder.getCustomerEmail(),
                savedOrder.getItems(),
                savedOrder.getTotalAmount().toString()
        );

        kafkaProducerService.publishOrderPlaced(event);

        return savedOrder;
    }
}
