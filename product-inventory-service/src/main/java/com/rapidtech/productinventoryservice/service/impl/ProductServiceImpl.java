package com.rapidtech.productinventoryservice.service.impl;

import com.rapidtech.productinventoryservice.dto.ProductRequest;
import com.rapidtech.productinventoryservice.dto.ProductResponse;
import com.rapidtech.productinventoryservice.model.Product;
import com.rapidtech.productinventoryservice.repository.ProductRepository;
import com.rapidtech.productinventoryservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    @SneakyThrows
    @Override
    public List<ProductResponse> isInStock(List<String> productName) {
        log.info("Mulai menunggu");
        //Thread.sleep(10000);
        log.info("Selesai menunggu");
        return productRepository.findByProductNameIn(productName).stream()
                .map(product ->
                    ProductResponse.builder()
                            .productName(product.getProductName())
                            .isInStcok(product.getQuantity()>0)
                            .build()).toList();
    }
    @Override
    public List<ProductRequest> getAll() {
        List<Product> products = productRepository.findAll();
        List<ProductRequest> productRequestList = new ArrayList<>();
        for (Product product : products) {
            productRequestList.add(ProductRequest.builder()
                            .productName(product.getProductName())
                            .price(product.getPrice())
                            .quantity(product.getQuantity())
                    .build());
        }
        return productRequestList;
    }

    @Override
    public void insertProduct(ProductRequest productRequest) {
        Product product = new Product();
        product.setProductName(productRequest.getProductName());
        product.setPrice(productRequest.getPrice());
        product.setQuantity(productRequest.getQuantity());
        productRepository.save(product);
    }
}
