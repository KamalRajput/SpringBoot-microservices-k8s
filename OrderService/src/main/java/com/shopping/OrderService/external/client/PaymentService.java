package com.shopping.OrderService.external.client;

import com.shopping.OrderService.exception.CustomException;
import com.shopping.OrderService.external.request.PaymentRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="PAYMENT-SERVICE/payment")
public interface PaymentService {

    @PostMapping
     ResponseEntity<Long> doPayment(@RequestBody PaymentRequest paymentRequest);

   }
