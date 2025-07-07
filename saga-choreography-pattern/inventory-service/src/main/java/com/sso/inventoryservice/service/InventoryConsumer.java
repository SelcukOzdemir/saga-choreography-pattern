package com.sso.inventoryservice.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.sso.inventoryservice.entity.Inventory;
import com.sso.inventoryservice.repository.InventoryRepository;
import com.sso.saga.events.InventoryFailedEvent;
import com.sso.saga.events.InventoryReservedEvent;
import com.sso.saga.events.PaymentCompletedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryConsumer {
	
	private final InventoryRepository inventoryRepository;
	private final KafkaTemplate<String, Object> kafkaTemplate;
	
	@KafkaListener(topics="payment-topic", groupId="inventory-group")
	public void consume(PaymentCompletedEvent event) {
		log.info("Received PaymentCompletedEvent for orderId={}", event.getOrderId());

        String productId = "product-001"; // sabit ürün simülasyonu
        Inventory inventory = inventoryRepository.findById(productId).orElse(null);

        if (inventory != null && inventory.getStock() > 0) {
            inventory.setStock(inventory.getStock() - 1);
            inventoryRepository.save(inventory);

            kafkaTemplate.send("inventory-topic", event.getOrderId(),
                    new InventoryReservedEvent(event.getOrderId(), productId));
        } else {
            kafkaTemplate.send("inventory-topic", event.getOrderId(),
                    new InventoryFailedEvent(event.getOrderId(), "Stock unavailable"));
        }
	}

}
