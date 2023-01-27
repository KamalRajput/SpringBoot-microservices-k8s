package com.shopping.PaymentService.service;

import com.shopping.PaymentService.model.PaymentRequest;
import com.shopping.PaymentService.model.PaymentResponse;

public interface PaymentService {
  
    Long doPayment(PaymentRequest paymentRequest);

    PaymentResponse getPaymentDetailsByOrderId(String orderId);
}
