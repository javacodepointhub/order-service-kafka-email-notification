package com.javacodepoint.orderservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
public class OrderRequest {
    @NotBlank
    private String customerName;

    @NotBlank
    @Email
    private String customerEmail;

    @NotBlank
    private String paymentMethod;

    @NotBlank
    private String shippingAddress;

    private List<OrderItemRequest> items;
}