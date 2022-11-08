package com.rapidtech.productinventoryservice.repository;

import com.rapidtech.productinventoryservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Long> {
    List<Product> findByProductNameIn(List<String> productName);
    Product findByProductName(String productName);
}
