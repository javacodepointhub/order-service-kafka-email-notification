package com.javacodepoint.orderservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderItemRequest {
    @NotBlank
    private String productName;
    private int quantity;
    private double price;
}
