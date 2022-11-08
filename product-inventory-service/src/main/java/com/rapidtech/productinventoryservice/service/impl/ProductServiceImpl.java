package com.rapidtech.productinventoryservice.service.impl;

import com.rapidtech.productinventoryservice.dto.ProductRequest;
import com.rapidtech.productinventoryservice.dto.ProductResponse;
import com.rapidtech.productinventoryservice.event.OrderPlacedEvent;
import com.rapidtech.productinventoryservice.model.Product;
import com.rapidtech.productinventoryservice.repository.ProductRepository;
import com.rapidtech.productinventoryservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;
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
                            .quantity(product.getQuantity())
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
    public ProductRequest cekStock(String productName) {
        Product product = productRepository.findByProductName(productName);
        return ProductRequest.builder()
                .productName((product.getProductName()))
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .build();
    }

    @Override
    public void insertProduct(ProductRequest productRequest) {
        Product product = new Product();
        product.setProductName(productRequest.getProductName());
        product.setPrice(productRequest.getPrice());
        product.setQuantity(productRequest.getQuantity());
        productRepository.save(product);
    }

    @Override
    public void increaseStock(ProductRequest productRequest) {
        Product product = productRepository.findByProductName(productRequest.getProductName());
        if (product != null) {
            product.setQuantity(product.getQuantity() + productRequest.getQuantity());
            productRepository.save(product);
        } else {
            throw new RuntimeException("Product tidak atau belum ada");
        }
    }

    @Transactional
    @Override
    public void decreaseStock(ProductRequest productRequest) {
        Product product = productRepository.findByProductName(productRequest.getProductName());
        if (product != null) {
            product.setQuantity(product.getQuantity() - productRequest.getQuantity());
            productRepository.save(product);
            kafkaTemplate.send("notificationTopic",new OrderPlacedEvent("Product name: "+product.getProductName()+" sisa stock: "+product.getQuantity()));
        } else {
            throw new RuntimeException("Product tidak atau belum ada");
        }
    }
}
