package com.rapidtech.orderservice.service.impl;

import com.rapidtech.orderservice.dto.OrderLineItemsDto;
import com.rapidtech.orderservice.dto.OrderReqDto;
import com.rapidtech.orderservice.dto.ProductResponse;
import com.rapidtech.orderservice.dto.WalletResponse;
import com.rapidtech.orderservice.event.OrderPlacedEvent;
import com.rapidtech.orderservice.model.Order;
import com.rapidtech.orderservice.model.OrderLineItems;
import com.rapidtech.orderservice.repository.OrderRepository;
import com.rapidtech.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final KafkaTemplate<String,OrderPlacedEvent> kafkaTemplate;

    @Override
    public String placeOrder(OrderReqDto orderReqDto) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> orderLineItems = orderReqDto.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto).toList();
        order.setOrderLineItemsList(orderLineItems);

        List<String> productNames = order.getOrderLineItemsList().stream()
                .map(OrderLineItems::getProductName).toList();

        //cek di product
        ProductResponse[] productResponsesArr = webClientBuilder.build().get().uri("http://product-service/api/product",
                        uriBuilder -> uriBuilder.queryParam("productName",productNames).build())
                .retrieve()
                .bodyToMono(ProductResponse[].class)
                .block();

        boolean allProductsInStock =
                Arrays.stream(productResponsesArr).allMatch(ProductResponse::isInStcok);

        if (allProductsInStock) {
            List<String> userNames = order.getOrderLineItemsList().stream()
                    .map(OrderLineItems::getUserName).toList();

            //cek di wallet
            WalletResponse[] walletResponsesArr = webClientBuilder.build().get().uri("http://wallet-service/api/wallet",
                            uriBuilder -> uriBuilder.queryParam("userName",userNames).build())
                    .retrieve()
                    .bodyToMono(WalletResponse[].class)
                    .block();

            boolean allWalletIsActive =
                    Arrays.stream(walletResponsesArr).allMatch(WalletResponse::isInActive);
            if (allWalletIsActive) {
                orderRepository.save(order);
                kafkaTemplate.send("notificationTopic",new OrderPlacedEvent(order.getOrderNumber()));

                return "Order berhasil...";
            } else {
                return "Saldo wallet tidak mencukupi";
            }
        } else {
            return "Stock product tidak mencukupi";
        }
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setUserName(orderLineItemsDto.getUserName());
        orderLineItems.setProductName(orderLineItemsDto.getProductName());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        return orderLineItems;

    }
}
