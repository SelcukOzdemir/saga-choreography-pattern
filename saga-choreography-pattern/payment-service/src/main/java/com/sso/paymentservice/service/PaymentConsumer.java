package com.sso.paymentservice.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.sso.paymentservice.entity.Payment;
import com.sso.paymentservice.entity.PaymentStatus;
import com.sso.paymentservice.repository.PaymentRepository;
import com.sso.saga.events.OrderCreatedEvent;
import com.sso.saga.events.PaymentCompletedEvent;
import com.sso.saga.events.PaymentFailedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentConsumer {
	
	private final PaymentRepository paymentRepository;
	private final KafkaTemplate< String, Object> kafkaTemplate;
	
	@KafkaListener(topics ="order-topic", groupId="payment-group")
	public void consume(OrderCreatedEvent event) {
		  log.info("Received OrderCreatedEvent for orderId={}", event.getOrderId());
		// Simüle edilmiş ödeme kontrolü (örneğin limit üstü ise başarısız)
	        boolean paymentSuccess = event.getAmount() <= 1000;

	        Payment payment = Payment.builder()
	                .orderId(event.getOrderId())
	                .userId(event.getUserId())
	                .amount(event.getAmount())
	                .status(paymentSuccess ? PaymentStatus.COMPLETED : PaymentStatus.FAILED)
	                .build();

	        paymentRepository.save(payment);

	        if (paymentSuccess) {
	            kafkaTemplate.send("payment-topic", event.getOrderId(),
	                    new PaymentCompletedEvent(event.getOrderId(), event.getUserId(), event.getAmount()));
	        } else {
	            kafkaTemplate.send("payment-topic", event.getOrderId(),
	                    new PaymentFailedEvent(event.getOrderId(), "Insufficient funds"));
	        }
	    }

}
