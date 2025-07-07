package com.sso.paymentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sso.paymentservice.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, String>{

}
