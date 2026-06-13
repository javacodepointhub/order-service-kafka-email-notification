package com.javacodepoint.orderservice.controller;

import com.javacodepoint.orderservice.dto.OrderRequest;
import com.javacodepoint.orderservice.model.Order;
import com.javacodepoint.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> placeOrder(@Valid @RequestBody OrderRequest request) {
        log.info("Received order placement request");
        Order order = orderService.placeOrder(request);
        return ResponseEntity.ok(order);
    }
}
