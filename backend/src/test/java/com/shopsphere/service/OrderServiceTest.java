package com.shopsphere.service;

import com.shopsphere.dto.OrderResponse;
import com.shopsphere.entity.*;
import com.shopsphere.exception.BadRequestException;
import com.shopsphere.repository.CartItemRepository;
import com.shopsphere.repository.OrderRepository;
import com.shopsphere.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock OrderRepository orderRepository;
    @Mock CartItemRepository cartItemRepository;
    @Mock UserRepository userRepository;
    @InjectMocks OrderService orderService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).name("Asha").email("asha@example.com").password("x").role(Role.USER).build();
    }

    @Test
    void placeOrder_deductsStock_clearsCart_andComputesTotal() {
        Product keyboard = Product.builder().id(5L).name("Keyboard").price(new BigDecimal("1000.00")).stock(10).build();
        CartItem item = CartItem.builder().id(1L).user(user).product(keyboard).quantity(3).build();
        when(userRepository.findByEmail("asha@example.com")).thenReturn(Optional.of(user));
        when(cartItemRepository.findByUser(user)).thenReturn(List.of(item));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderResponse res = orderService.placeOrder("asha@example.com");

        assertEquals(0, new BigDecimal("3000.00").compareTo(res.total()));
        assertEquals(OrderStatus.PENDING, res.status());
        assertEquals(7, keyboard.getStock());
        verify(cartItemRepository).deleteByUser(user);
    }

    @Test
    void placeOrder_emptyCart_throwsBadRequest() {
        when(userRepository.findByEmail("asha@example.com")).thenReturn(Optional.of(user));
        when(cartItemRepository.findByUser(user)).thenReturn(List.of());

        assertThrows(BadRequestException.class, () -> orderService.placeOrder("asha@example.com"));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void placeOrder_insufficientStock_throwsBadRequest() {
        Product mouse = Product.builder().id(6L).name("Mouse").price(new BigDecimal("500.00")).stock(1).build();
        CartItem item = CartItem.builder().id(2L).user(user).product(mouse).quantity(2).build();
        when(userRepository.findByEmail("asha@example.com")).thenReturn(Optional.of(user));
        when(cartItemRepository.findByUser(user)).thenReturn(List.of(item));

        assertThrows(BadRequestException.class, () -> orderService.placeOrder("asha@example.com"));
        assertEquals(1, mouse.getStock());
    }

    @Test
    void updateStatus_cancel_restoresStock() {
        Product p = Product.builder().id(7L).name("Hub").price(new BigDecimal("100.00")).stock(4).build();
        Order order = Order.builder().id(9L).user(user).status(OrderStatus.PENDING)
                .total(new BigDecimal("200.00")).createdAt(java.time.LocalDateTime.now()).build();
        order.getItems().add(OrderItem.builder().order(order).product(p).productName("Hub")
                .price(new BigDecimal("100.00")).quantity(2).build());
        when(orderRepository.findById(9L)).thenReturn(Optional.of(order));

        OrderResponse res = orderService.updateStatus(9L, OrderStatus.CANCELLED);

        assertEquals(OrderStatus.CANCELLED, res.status());
        assertEquals(6, p.getStock());
    }
}
