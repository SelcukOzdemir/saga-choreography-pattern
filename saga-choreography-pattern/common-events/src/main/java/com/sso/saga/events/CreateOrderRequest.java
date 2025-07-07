package com.sso.saga.events;

import lombok.Data;

@Data
public class CreateOrderRequest {

	 private String userId;
	 private Double amount;
}
