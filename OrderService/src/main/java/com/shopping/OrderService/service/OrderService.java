package com.shopping.OrderService.service;

import com.shopping.OrderService.model.OrderRequest;
import com.shopping.OrderService.model.OrderResponse;

public interface OrderService {
    Long placeOrder(OrderRequest orderRequest);

    OrderResponse getOrderDetails(long orderId);
}
