package com.rapidtech.orderservice.service.impl;

import com.rapidtech.orderservice.dto.*;
import com.rapidtech.orderservice.event.OrderPlacedEvent;
import com.rapidtech.orderservice.model.Order;
import com.rapidtech.orderservice.model.OrderLineItems;
import com.rapidtech.orderservice.repository.OrderRepository;
import com.rapidtech.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final KafkaTemplate<String,OrderPlacedEvent> kafkaTemplate;

    @Transactional
    @Override
    public String placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setUserName(orderRequest.getUserName());
        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto).toList();
        order.setOrderLineItemsList(orderLineItems);

        List<Boolean> stockAvailable = new ArrayList<Boolean>();
        Double totalPrice = 0.00;
        for (OrderLineItems orderLine : orderLineItems) {
            String productName = orderLine.getProductName();
            ProductResponse productResponse = webClientBuilder.build().get()
                    .uri("http://product-service/api/product/cekstock",
                            uriBuilder -> uriBuilder.queryParam("productName", productName).build())
                    .retrieve()
                    .bodyToMono(ProductResponse.class)
                    .block();

            Integer quantityRequest = orderLine.getQuantity();
            Integer quantityProduct = productResponse.getQuantity();
            Boolean available = stockAvailable(quantityProduct, quantityRequest);
            stockAvailable.add(available);

            double productPrice = productResponse.getPrice() * orderLine.getQuantity();
            totalPrice = totalPrice + productPrice;
        }
        Boolean allStockAvailable = stockAvailable.contains(false);
        allStockAvailable = !allStockAvailable;
        if (!allStockAvailable) {
            return "Stock tidak memadai";
        }

        String userName = order.getUserName();
        WalletResponse walletResponse = webClientBuilder.build().get()
                .uri("http://wallet-service/api/wallet/ceksaldo",
                        uriBuilder -> uriBuilder.queryParam("userName", userName).build())
                .retrieve()
                .bodyToMono(WalletResponse.class)
                .block();

        double saldo = walletResponse.getSaldo();
        Boolean saldoAvailable = saldoAvailable(saldo, totalPrice);
        if (!saldoAvailable) {
            return "Saldo tidak memadai";
        }

        for (OrderLineItems orderLine : orderLineItems) {
            ProductDto decreaseStock = ProductDto.builder()
                    .productName(orderLine.getProductName())
                    .quantity(orderLine.getQuantity())
                    .build();
            ProductResponse newStock = webClientBuilder.build()
                    .post()
                    .uri("http://product-service/api/product/decrease")
                    .accept(MediaType.APPLICATION_JSON)
                    .body(Mono.just(decreaseStock), ProductDto.class)
                    .retrieve()
                    .bodyToMono(ProductResponse.class)
                    .block();
        }

        WalletResponse decreaseSaldo = WalletResponse.builder()
                .userName(walletResponse.getUserName())
                .saldo(totalPrice)
                .build();
        WalletResponse newSaldo = webClientBuilder.build()
                .post()
                .uri("http://wallet-service/api/wallet/decrease")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(decreaseSaldo), WalletResponse.class)
                .retrieve()
                .bodyToMono(WalletResponse.class)
                .block();

        List<Boolean> availableList = new ArrayList<Boolean>();
        availableList.add(saldoAvailable);
        availableList.addAll(stockAvailable);

        boolean allAvailable = availableList.contains(false);
        allAvailable = !allAvailable;
        if (allAvailable) {
            orderRepository.save(order);
            kafkaTemplate.send("notificationTopic", new OrderPlacedEvent("Order number: "+order.getOrderNumber()));
        }
        return "Order berhasil !";
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setProductName(orderLineItemsDto.getProductName());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        return orderLineItems;

    }

    private boolean stockAvailable(Integer stockProduct, Integer stockRequest) {
        boolean check;
        check = stockProduct >= stockRequest;
        return check;
    }

    private boolean saldoAvailable(double saldo, double totalPrice) {
        boolean check;
        check = saldo >= totalPrice;
        return check;
    }
}
