package com.shopsphere.service;

import com.shopsphere.dto.ProductRequest;
import com.shopsphere.dto.ProductResponse;
import com.shopsphere.entity.Product;
import com.shopsphere.exception.ResourceNotFoundException;
import com.shopsphere.repository.ProductRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Page<ProductResponse> search(String q, String category, BigDecimal minPrice,
                                        BigDecimal maxPrice, Pageable pageable) {
        Specification<Product> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (q != null && !q.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + q.trim().toLowerCase() + "%"));
            }
            if (category != null && !category.isBlank()) {
                predicates.add(cb.equal(root.get("category"), category));
            }
            if (minPrice != null) predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            if (maxPrice != null) predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return productRepository.findAll(spec, pageable).map(ProductResponse::from);
    }

    @Transactional(readOnly = true)
    public List<String> categories() {
        return productRepository.findDistinctCategories();
    }

    @Transactional(readOnly = true)
    public ProductResponse get(Long id) {
        return ProductResponse.from(find(id));
    }

    @Transactional
    public ProductResponse create(ProductRequest req) {
        Product product = new Product();
        apply(product, req);
        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest req) {
        Product product = find(id);
        apply(product, req);
        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional
    public void delete(Long id) {
        productRepository.delete(find(id));
    }

    private Product find(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
    }

    private void apply(Product p, ProductRequest r) {
        p.setName(r.name().trim());
        p.setDescription(r.description());
        p.setPrice(r.price());
        p.setStock(r.stock());
        p.setImageUrl(r.imageUrl());
        p.setCategory(r.category());
    }
}
