package com.shopsphere.controller;

import com.shopsphere.dto.OrderResponse;
import com.shopsphere.dto.StatusUpdateRequest;
import com.shopsphere.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/api/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse place(Principal principal) {
        return orderService.placeOrder(principal.getName());
    }

    @GetMapping("/api/orders")
    public List<OrderResponse> mine(Principal principal) {
        return orderService.myOrders(principal.getName());
    }

    @GetMapping("/api/admin/orders")
    public List<OrderResponse> all() {
        return orderService.allOrders();
    }

    @PatchMapping("/api/admin/orders/{id}/status")
    public OrderResponse updateStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateRequest req) {
        return orderService.updateStatus(id, req.status());
    }
}
