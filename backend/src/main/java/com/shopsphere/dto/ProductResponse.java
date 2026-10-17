package com.shopsphere.dto;

import com.shopsphere.entity.Product;
import java.math.BigDecimal;

public record ProductResponse(Long id, String name, String description, BigDecimal price,
                              int stock, String imageUrl, String category) {
    public static ProductResponse from(Product p) {
        return new ProductResponse(p.getId(), p.getName(), p.getDescription(), p.getPrice(),
                p.getStock(), p.getImageUrl(), p.getCategory());
    }
}
