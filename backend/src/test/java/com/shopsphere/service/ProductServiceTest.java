package com.shopsphere.service;

import com.shopsphere.dto.ProductRequest;
import com.shopsphere.dto.ProductResponse;
import com.shopsphere.entity.Product;
import com.shopsphere.exception.ResourceNotFoundException;
import com.shopsphere.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock ProductRepository productRepository;
    @InjectMocks ProductService productService;

    @Test
    void create_savesProductAndReturnsResponse() {
        ProductRequest req = new ProductRequest("Mouse", "Wireless", new BigDecimal("499.00"), 10, null, "Accessories");
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> {
            Product p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });

        ProductResponse res = productService.create(req);

        assertEquals(1L, res.id());
        assertEquals("Mouse", res.name());
        assertEquals(10, res.stock());
    }

    @Test
    void get_unknownId_throwsNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> productService.get(99L));
    }

    @Test
    void delete_existingProduct_removesIt() {
        Product p = Product.builder().id(2L).name("Hub").price(BigDecimal.TEN).stock(1).build();
        when(productRepository.findById(2L)).thenReturn(Optional.of(p));

        productService.delete(2L);

        verify(productRepository).delete(p);
    }
}
