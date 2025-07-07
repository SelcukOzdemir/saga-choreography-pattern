package com.sso.inventoryservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sso.inventoryservice.entity.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, String>{

}
