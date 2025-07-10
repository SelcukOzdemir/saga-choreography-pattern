package com.sso.jwttoken.service;


import com.sso.jwttoken.entity.RefreshToken;
import com.sso.jwttoken.entity.User;
import com.sso.jwttoken.repository.RefreshTokenRepository;
import com.sso.jwttoken.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    private final long refreshTokenDurationMs = 7 * 24 * 60 * 60 * 1000; // 7 gün

    @Transactional
    public RefreshToken createRefreshToken(String username) {
        User user = userRepository.findByUsername(username)
                      .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        refreshTokenRepository.findByUser(user)
        .ifPresent(refreshTokenRepository::delete);
        
        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));

        return refreshTokenRepository.save(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token süresi dolmuş. Lütfen tekrar login olun.");
        }
        return token;
    }
    
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public void deleteByUserId(Long userId) {
        User user = userRepository.findById(userId)
                       .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        refreshTokenRepository.deleteByUser(user);
    }
}
