package com.sso.jwttoken.service;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sso.jwttoken.entity.User;
import com.sso.jwttoken.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService{
	
	private final UserRepository userRepository;
	

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		User user =  userRepository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException(username));
		
		  return new org.springframework.security.core.userdetails.User(
		            user.getUsername(),
		            user.getPassword(),
		            Collections.singletonList(new SimpleGrantedAuthority(user.getRole()))
		        );
	}

}
