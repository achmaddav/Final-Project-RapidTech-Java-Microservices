package com.rapidtech.orderservice.service;

import com.rapidtech.orderservice.dto.OrderReqDto;

public interface OrderService {

    public String placeOrder(OrderReqDto orderReqDto);
}
