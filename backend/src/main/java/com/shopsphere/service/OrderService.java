package com.shopsphere.service;

import com.shopsphere.dto.OrderResponse;
import com.shopsphere.entity.*;
import com.shopsphere.exception.BadRequestException;
import com.shopsphere.exception.ResourceNotFoundException;
import com.shopsphere.repository.CartItemRepository;
import com.shopsphere.repository.OrderRepository;
import com.shopsphere.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    /** Converts the user's cart into an order, deducting stock, all in one transaction. */
    @Transactional
    public OrderResponse placeOrder(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
        List<CartItem> cart = cartItemRepository.findByUser(user);
        if (cart.isEmpty()) {
            throw new BadRequestException("Your cart is empty");
        }

        Order order = Order.builder().user(user).status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now()).total(BigDecimal.ZERO).build();
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem ci : cart) {
            Product product = ci.getProduct();
            if (ci.getQuantity() > product.getStock()) {
                throw new BadRequestException("Not enough stock for '" + product.getName()
                        + "' (available: " + product.getStock() + ")");
            }
            product.setStock(product.getStock() - ci.getQuantity());
            order.getItems().add(OrderItem.builder().order(order).product(product)
                    .productName(product.getName()).price(product.getPrice())
                    .quantity(ci.getQuantity()).build());
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())));
        }
        order.setTotal(total);

        Order saved = orderRepository.save(order);
        cartItemRepository.deleteByUser(user);
        return OrderResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> myOrders(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
        return orderRepository.findByUserOrderByCreatedAtDesc(user).stream().map(OrderResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> allOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc().stream().map(OrderResponse::from).toList();
    }

    @Transactional
    public OrderResponse updateStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException("Cancelled orders cannot be changed");
        }
        if (status == OrderStatus.CANCELLED) {
            order.getItems().forEach(i -> i.getProduct().setStock(i.getProduct().getStock() + i.getQuantity()));
        }
        order.setStatus(status);
        return OrderResponse.from(order);
    }
}
