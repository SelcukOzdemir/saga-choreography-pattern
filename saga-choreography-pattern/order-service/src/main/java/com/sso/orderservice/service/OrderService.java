package com.sso.orderservice.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.sso.orderservice.entity.Order;
import com.sso.orderservice.entity.OrderRepository;
import com.sso.orderservice.entity.OrderStatus;
import com.sso.saga.events.OrderCreatedEvent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
	
	private final OrderRepository orderRepository;
	private final KafkaTemplate< String, Object> kafkaTemplate;
	
	public Order createOrder(String userId, Double amount) {
		
		String orderId = UUID.randomUUID().toString();
		Order order = Order.builder()
				.id(orderId)
				.userId(userId)
				.amount(amount)
				.status(OrderStatus.PENDING).build();
		
		orderRepository.save(order);
		
		OrderCreatedEvent orderCreatedEvent = OrderCreatedEvent.builder()
				.orderId(orderId)
				.userId(userId)
				.amount(amount)
				.createdAt(LocalDateTime.now()).build();
		
		kafkaTemplate.send("order-topic", orderId, orderCreatedEvent);
		
		return order;
		
	}

}
