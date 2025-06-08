package com.sso.rediscache.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sso.rediscache.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
