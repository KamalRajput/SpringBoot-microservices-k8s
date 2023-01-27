package com.shopping.PaymentService.service;

import com.shopping.PaymentService.entity.TransactionDetails;
import com.shopping.PaymentService.model.PaymentMode;
import com.shopping.PaymentService.model.PaymentRequest;
import com.shopping.PaymentService.model.PaymentResponse;
import com.shopping.PaymentService.repository.TransactionDetailsRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@Log4j2
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private TransactionDetailsRepository transactionDetailsRepository;

    @Override
    public Long doPayment(PaymentRequest paymentRequest) {
        log.info("Recording Payment Details: {}", paymentRequest);
        TransactionDetails txnDetails = TransactionDetails.builder()
                .paymentDate(Instant.now())
                .paymentStatus("SUCCESS")
                .paymentMode(paymentRequest.getPaymentMode().name())
                .amount(paymentRequest.getAmount())
                .orderId(paymentRequest.getOrderId())
                .referenceNumber(paymentRequest.getReferenceNumber())
                .build();
        transactionDetailsRepository.save(txnDetails);
        log.info("transaction completed with id: {}", txnDetails.getId());
        return txnDetails.getId();
    }

    @Override
    public PaymentResponse getPaymentDetailsByOrderId(String orderId) {
        log.info("Getting payment details for the Order Id: {}", orderId);
        TransactionDetails transactionDetails
                = transactionDetailsRepository.findByOrderId(Long.valueOf(orderId));

        log.info("transaction Details: {}", transactionDetails);
        PaymentResponse paymentResponse
                = PaymentResponse.builder()
                .paymentId(transactionDetails.getId())
                .paymentMode(PaymentMode.valueOf(transactionDetails.getPaymentMode()))
                .paymentDate(transactionDetails.getPaymentDate())
                .orderId(transactionDetails.getOrderId())
                .status(transactionDetails.getPaymentStatus())
                .amount(transactionDetails.getAmount())
                .build();

        return paymentResponse;
    }

}
