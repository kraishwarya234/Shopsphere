package com.shopsphere.repository;

import com.shopsphere.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    @Query("select distinct p.category from Product p where p.category is not null order by p.category")
    List<String> findDistinctCategories();
}
