package com.javacodepoint.orderservice.model;

import jakarta.persistence.Embeddable;
import lombok.Data;
import java.math.BigDecimal;

@Embeddable
@Data
public class OrderItem {
    private String productName;
    private int quantity;
    private BigDecimal price;
}