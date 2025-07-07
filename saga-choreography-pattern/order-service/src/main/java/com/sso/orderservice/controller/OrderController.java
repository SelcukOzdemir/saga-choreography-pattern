package com.sso.orderservice.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sso.orderservice.entity.Order;
import com.sso.orderservice.service.OrderService;
import com.sso.saga.events.CreateOrderRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
	
	private final OrderService  orderService;
	
	@PostMapping
	public Order createOrder(@RequestBody CreateOrderRequest request) {
		
		return orderService.createOrder(request.getUserId(), request.getAmount());
		
	}

}
