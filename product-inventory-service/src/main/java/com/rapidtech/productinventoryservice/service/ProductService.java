package com.rapidtech.productinventoryservice.service;

import com.rapidtech.productinventoryservice.dto.ProductRequest;
import com.rapidtech.productinventoryservice.dto.ProductResponse;

import java.util.List;

public interface ProductService {
    List<ProductResponse> isInStock(List<String> productName);
    List<ProductRequest> getAll();
    ProductRequest cekStock(String userName);
    void insertProduct(ProductRequest productRequest);
    void increaseStock(ProductRequest productRequest);
    void decreaseStock(ProductRequest productRequest);
}
