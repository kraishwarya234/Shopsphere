package com.shopsphere.controller;

import com.shopsphere.dto.CartItemRequest;
import com.shopsphere.dto.CartResponse;
import com.shopsphere.dto.UpdateQuantityRequest;
import com.shopsphere.service.CartService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Cart")
public class CartController {
    private final CartService cartService;

    @GetMapping
    public CartResponse get(Principal principal) {
        return cartService.getCart(principal.getName());
    }

    @PostMapping("/items")
    public CartResponse add(Principal principal, @Valid @RequestBody CartItemRequest req) {
        return cartService.addItem(principal.getName(), req);
    }

    @PutMapping("/items/{itemId}")
    public CartResponse update(Principal principal, @PathVariable Long itemId,
                               @Valid @RequestBody UpdateQuantityRequest req) {
        return cartService.updateQuantity(principal.getName(), itemId, req.quantity());
    }

    @DeleteMapping("/items/{itemId}")
    public CartResponse remove(Principal principal, @PathVariable Long itemId) {
        return cartService.removeItem(principal.getName(), itemId);
    }
}
