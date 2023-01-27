package com.shopping.OrderService.service;

import com.shopping.OrderService.entity.Order;
import com.shopping.OrderService.exception.CustomException;
import com.shopping.OrderService.external.client.PaymentService;
import com.shopping.OrderService.external.client.ProductService;
import com.shopping.OrderService.external.request.PaymentRequest;
import com.shopping.OrderService.external.response.PaymentResponse;
import com.shopping.OrderService.external.response.ProductResponse;
import com.shopping.OrderService.model.OrderRequest;
import com.shopping.OrderService.model.OrderResponse;
import com.shopping.OrderService.repository.OrderRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Long placeOrder(OrderRequest orderRequest) {
        // Order Entity -save the order
        // Block the products (reduce the quantity)
        // call payment service to complete payment, if success, mark complete else cancelled

        log.info("placing order request: {}", orderRequest);
        productService.reduceQuantity(orderRequest.getProductId(), orderRequest.getQuantity());
        Order order = Order.builder().
                amount(orderRequest.getTotalAmount())
                .quantity(orderRequest.getQuantity())
                .orderStatus("CREATED")
                .productId(orderRequest.getProductId())
                .OrderDate(Instant.now())
                .build();
        orderRepository.save(order);
        log.info("Calling Payment service to complete the payment");
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(order.getId())
                .paymentMode(orderRequest.getPaymentMode())
                .amount(orderRequest.getTotalAmount())
                .build();

        String orderStatus = null;
        try {
            paymentService.doPayment(paymentRequest);
            log.info("payment done successfully, changing orderStatus to PLACED");
            orderStatus = "PLACED";
        } catch (Exception e) {
            log.info("Error occured in payment, changing orderStatus to FAILED");
            orderStatus = "PAYMENT_FAILED";
        }
        order.setOrderStatus(orderStatus);
        orderRepository.save(order);
        log.info("order placed successfully with id: " + order.getId());
        return order.getId();
    }

    @Override
    public OrderResponse getOrderDetails(long orderId) {
        log.info("getting order details by orderId: {}",orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException("Order not found for the order id", "ORDER_NOT_FOUND", 404));

        log.info("Invoking product service to fetch the product details for id : {}",order.getProductId());
        ProductResponse productResponse = restTemplate.getForObject("http://PRODUCT-SERVICE/product/"+order.getProductId(),
                ProductResponse.class);

        log.info("Getting payment information from the payment service for order id :{}",order.getId());
        PaymentResponse paymentResponse =restTemplate.getForObject("http://PAYMENT-SERVICE/payment/order/"+order.getId(), PaymentResponse.class);
        OrderResponse.ProductDetails productDetails =OrderResponse.ProductDetails.builder()
                .productName(productResponse.getProductName())
                .price(productResponse.getPrice())
                .quantity(productResponse.getQuantity())
                .productId(productResponse.getProductId())
                .build();

        OrderResponse.PaymentDetails paymentDetails =OrderResponse.PaymentDetails.builder()
                .paymentId(paymentResponse.getPaymentId())
                .status(paymentResponse.getStatus())
                .paymentMode(paymentResponse.getPaymentMode())
                .paymentDate(paymentResponse.getPaymentDate())
                .build();
        OrderResponse orderResponse = OrderResponse.builder()
                .orderId(order.getId())
                .orderStatus(order.getOrderStatus())
                .amount(order.getAmount())
                .orderDate(order.getOrderDate())
                .productDetails(productDetails)
                .paymentDetails(paymentDetails)
                .build();
        return orderResponse;
    }
}
