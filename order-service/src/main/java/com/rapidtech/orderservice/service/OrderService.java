package com.rapidtech.orderservice.service;

import com.rapidtech.orderservice.dto.OrderRequest;

public interface OrderService {

    public String placeOrder(OrderRequest orderReqDto);
}
