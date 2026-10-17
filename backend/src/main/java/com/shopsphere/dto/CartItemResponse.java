package com.shopsphere.dto;

import com.shopsphere.entity.CartItem;
import java.math.BigDecimal;

public record CartItemResponse(Long id, Long productId, String name, String imageUrl,
                               BigDecimal price, int quantity, BigDecimal subtotal) {
    public static CartItemResponse from(CartItem c) {
        BigDecimal price = c.getProduct().getPrice();
        return new CartItemResponse(c.getId(), c.getProduct().getId(), c.getProduct().getName(),
                c.getProduct().getImageUrl(), price, c.getQuantity(),
                price.multiply(BigDecimal.valueOf(c.getQuantity())));
    }
}
