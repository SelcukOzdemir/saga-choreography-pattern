package com.sso.jwttoken.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtService {
	
	@Value("${jwt_token}")
	private String secret;
	
	private final long jwtExpirationInMs = 15 * 60 * 1000;
	
	 public String generateToken(UserDetails userDetails) {
	        Map<String, Object> claims = new HashMap<>();
	        
	        List<String> roles = userDetails.getAuthorities()
	                .stream()
	                .map(authority -> authority.getAuthority())
	                .toList();

	            claims.put("roles", roles);

	        return Jwts.builder()
	                .setClaims(claims)
	                .setSubject(userDetails.getUsername())
	                .setIssuedAt(new Date())
	                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
	                .signWith(SignatureAlgorithm.HS256, secret)
	                .compact();
	    }

	    public String extractUsername(String token) {
	        return extractClaims(token).getSubject();
	    }

	    public boolean validateToken(String token, UserDetails userDetails) {
	        final String username = extractUsername(token);
	        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	    }

	    private boolean isTokenExpired(String token) {
	        return extractClaims(token).getExpiration().before(new Date());
	    }

	    private Claims extractClaims(String token) {
	        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	    }
	

}
