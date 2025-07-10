package com.sso.jwttoken.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sso.jwttoken.dto.AuthRequest;
import com.sso.jwttoken.dto.AuthResponse;
import com.sso.jwttoken.dto.TokenRefreshRequest;
import com.sso.jwttoken.entity.RefreshToken;
import com.sso.jwttoken.entity.User;
import com.sso.jwttoken.repository.UserRepository;
import com.sso.jwttoken.service.CustomUserDetailsService;
import com.sso.jwttoken.service.JwtService;
import com.sso.jwttoken.service.RefreshTokenService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {

     private final AuthenticationManager authenticationManager;
     private final JwtService jwtService;
     private final UserRepository userRepository;
     private final PasswordEncoder passwordEncoder;
     private final RefreshTokenService refreshTokenService;
     private final CustomUserDetailsService userDetailsService;


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {
    	if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Bu kullanıcı adı zaten mevcut");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("ROLE_USER");

        userRepository.save(user);
        return ResponseEntity.ok("Kayıt başarılı");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());

        String accessToken = jwtService.generateToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(request.getUsername());

        return ResponseEntity.ok(
            new AuthResponse(accessToken, refreshToken.getToken())
        );
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest request) {
        String requestToken = request.getRefreshToken();

        RefreshToken token = refreshTokenService
                                .findByToken(requestToken)
                                .map(refreshTokenService::verifyExpiration)
                                .orElseThrow(() -> new RuntimeException("Geçersiz refresh token"));

        String username = token.getUser().getUsername();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String newAccessToken = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(newAccessToken, requestToken));
    }

    @GetMapping("/secure")
    public ResponseEntity<String> secureEndpoint() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok("Hoş geldin: " + username);
    }
}
