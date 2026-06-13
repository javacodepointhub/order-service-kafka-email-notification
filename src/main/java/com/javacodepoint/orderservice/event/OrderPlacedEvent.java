package com.javacodepoint.orderservice.event;

import com.javacodepoint.orderservice.model.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderPlacedEvent {
    private Long orderId;
    private String orderNumber;
    private String customerName;
    private String customerEmail;
    private List<OrderItem> items; // simplified
    private String totalAmount;
}
