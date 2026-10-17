package com.shopsphere.dto;

import com.shopsphere.entity.Order;
import com.shopsphere.entity.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(Long id, String userEmail, OrderStatus status, BigDecimal total,
                            LocalDateTime createdAt, List<OrderItemResponse> items) {
    public static OrderResponse from(Order o) {
        return new OrderResponse(o.getId(), o.getUser().getEmail(), o.getStatus(), o.getTotal(),
                o.getCreatedAt(), o.getItems().stream().map(OrderItemResponse::from).toList());
    }
}
