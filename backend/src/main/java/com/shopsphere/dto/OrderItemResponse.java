package com.shopsphere.dto;

import com.shopsphere.entity.OrderItem;
import java.math.BigDecimal;

public record OrderItemResponse(Long productId, String productName, BigDecimal price, int quantity) {
    public static OrderItemResponse from(OrderItem i) {
        return new OrderItemResponse(i.getProduct().getId(), i.getProductName(), i.getPrice(), i.getQuantity());
    }
}
