package com.rapidtech.orderservice.controller;

import com.rapidtech.orderservice.dto.OrderRequest;
import com.rapidtech.orderservice.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "product",fallbackMethod = "fallbackMethod")
    @TimeLimiter(name = "product")
    @Retry(name = "product")
    public CompletableFuture<String> placeOrder(@RequestBody OrderRequest orderReqDto) {
        return CompletableFuture.supplyAsync(()->orderService.placeOrder(orderReqDto));
    }
    public CompletableFuture<String> fallbackMethod(RuntimeException runtimeException) {
        return CompletableFuture.supplyAsync(()->"Terjadi kesalahan, silahkan untuk order kembali beberapa saat lagi...");
    }
}
