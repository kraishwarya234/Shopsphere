package com.shopsphere.service;

import com.shopsphere.dto.CartItemRequest;
import com.shopsphere.dto.CartItemResponse;
import com.shopsphere.dto.CartResponse;
import com.shopsphere.entity.CartItem;
import com.shopsphere.entity.Product;
import com.shopsphere.entity.User;
import com.shopsphere.exception.BadRequestException;
import com.shopsphere.exception.ResourceNotFoundException;
import com.shopsphere.repository.CartItemRepository;
import com.shopsphere.repository.ProductRepository;
import com.shopsphere.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public CartResponse getCart(String email) {
        return toResponse(cartItemRepository.findByUser(findUser(email)));
    }

    @Transactional
    public CartResponse addItem(String email, CartItemRequest req) {
        User user = findUser(email);
        Product product = productRepository.findById(req.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + req.productId()));
        CartItem item = cartItemRepository.findByUserAndProduct(user, product)
                .orElseGet(() -> CartItem.builder().user(user).product(product).quantity(0).build());
        int newQty = item.getQuantity() + req.quantity();
        checkStock(product, newQty);
        item.setQuantity(newQty);
        cartItemRepository.save(item);
        return toResponse(cartItemRepository.findByUser(user));
    }

    @Transactional
    public CartResponse updateQuantity(String email, Long itemId, int quantity) {
        User user = findUser(email);
        CartItem item = ownedItem(user, itemId);
        checkStock(item.getProduct(), quantity);
        item.setQuantity(quantity);
        return toResponse(cartItemRepository.findByUser(user));
    }

    @Transactional
    public CartResponse removeItem(String email, Long itemId) {
        User user = findUser(email);
        cartItemRepository.delete(ownedItem(user, itemId));
        return toResponse(cartItemRepository.findByUser(user));
    }

    private void checkStock(Product product, int qty) {
        if (qty > product.getStock()) {
            throw new BadRequestException("Only " + product.getStock() + " unit(s) of '" + product.getName() + "' in stock");
        }
    }

    private CartItem ownedItem(User user, Long itemId) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found: " + itemId));
        if (!item.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Cart item not found: " + itemId);
        }
        return item;
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    private CartResponse toResponse(List<CartItem> items) {
        List<CartItemResponse> list = items.stream().map(CartItemResponse::from).toList();
        BigDecimal total = list.stream().map(CartItemResponse::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CartResponse(list, total);
    }
}
