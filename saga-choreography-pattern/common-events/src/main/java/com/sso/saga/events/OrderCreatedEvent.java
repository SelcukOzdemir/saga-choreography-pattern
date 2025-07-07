package com.sso.saga.events;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderCreatedEvent {
    private String orderId;
    private String userId;
    private double amount;
    private LocalDateTime createdAt;
}
