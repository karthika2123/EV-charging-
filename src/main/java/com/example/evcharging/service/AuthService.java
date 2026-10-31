package com.example.evcharging.service;

import com.example.evcharging.dto.AuthResponse;
import com.example.evcharging.dto.LoginRequest;
import com.example.evcharging.dto.RegisterRequest;
import com.example.evcharging.dto.UserResponse;
import com.example.evcharging.entity.Role;
import com.example.evcharging.entity.User;
import com.example.evcharging.exception.BadRequestException;
import com.example.evcharging.exception.ResourceNotFoundException;
import com.example.evcharging.repository.UserRepository;
import com.example.evcharging.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        Role role = request.getRole();
        if (role == null) {
            role = Role.CUSTOMER;
        }
        // nobody can make themselves admin
        if (role == Role.ADMIN) {
            throw new BadRequestException("You cannot register as ADMIN");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // hash the password
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole(role);

        User saved = userRepository.save(user);
        return new UserResponse(saved.getId(), saved.getName(), saved.getEmail(),
                saved.getRole().name(), saved.getPhoneNumber());
    }

    public AuthResponse login(LoginRequest request) {
        // throws BadCredentialsException if email/password is wrong
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token, user.getId(), user.getEmail(), user.getRole().name());
    }

    // returns the user who is logged in right now (from the JWT token)
    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
