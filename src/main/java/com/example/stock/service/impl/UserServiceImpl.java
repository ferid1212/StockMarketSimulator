package com.example.stock.service.impl;


import com.example.stock.dto.request.RegisterRequest;
import com.example.stock.dto.request.UserRequest;
import com.example.stock.dto.response.UserResponse;
import com.example.stock.entity.Basket;
import com.example.stock.entity.User;
import com.example.stock.enums.Role;
import com.example.stock.mapper.UserMapper;
import com.example.stock.repository.UserRepository;
import com.example.stock.service.serviceInterface.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse create(UserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new IllegalArgumentException("Bu email artıq istifadə olunur: " + userRequest.getEmail());
        }

        Role userRole = Role.ROLE_USER;
        if (userRequest.getRole() != null) {
            try {
                userRole = Role.valueOf(userRequest.getRole());
            } catch (Exception ignored) {}
        }

        String rawPassword = userRequest.getPassword() != null && !userRequest.getPassword().isBlank() 
                ? userRequest.getPassword() : "123456";

        User user = User.builder()
                .name(userRequest.getName())
                .surname(userRequest.getSurname())
                .age(userRequest.getAge())
                .phone(userRequest.getPhone())
                .email(userRequest.getEmail())
                .password(passwordEncoder.encode(rawPassword))
                .role(userRole)
                .balance(userRequest.getBalance() != null ? userRequest.getBalance() : 0.0)
                .build();

        Basket basket = new Basket();
        basket.setUser(user);
        user.setBasket(basket);

        userRepository.save(user);
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Bu email ünvanı artıq qeydiyyatdan keçib!");
        }

        User user = User.builder()
                .name(registerRequest.getName())
                .surname(registerRequest.getSurname())
                .age(registerRequest.getAge())
                .phone(registerRequest.getPhone())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.ROLE_USER)
                .balance(0.0)
                .build();

        Basket basket = new Basket();
        basket.setUser(user);
        user.setBasket(basket);

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Override
    public List<UserResponse> allUsers() {
        List<User> users = userRepository.findAll();
        return userMapper.toResponseList(users);
    }

    @Override
    public UserResponse findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
        return userMapper.toResponse(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void addBalance(Long userId, Double amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        Double currentBalance = user.getBalance() != null ? user.getBalance() : 0.0;
        user.setBalance(currentBalance + amount);
        userRepository.save(user);
    }
}
