package com.rapidtech.productinventoryservice.controller;

import com.rapidtech.productinventoryservice.dto.ProductRequest;
import com.rapidtech.productinventoryservice.dto.ProductResponse;
import com.rapidtech.productinventoryservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> isInStock(@RequestParam List<String> productName) {
        return productService.isInStock(productName);
    }

    @GetMapping("/getAllProducts")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductRequest> getAll() {
        return productService.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String insertProduct(@RequestBody ProductRequest productRequest) {
        productService.insertProduct(productRequest);
        return "Data product added";
    }

    @GetMapping("/cekstock")
    public ProductRequest cekstock(@RequestParam String productName){

        return productService.cekStock(productName);
    }


    @PostMapping("/increase")
    public String increase(@RequestBody ProductRequest productRequest) {
        productService.increaseStock(productRequest);
        return "Stock product is added";
    }

    @PostMapping("/decrease")
    public void decrease(@RequestBody ProductRequest productRequest) {
        productService.decreaseStock(productRequest);
    }
}
