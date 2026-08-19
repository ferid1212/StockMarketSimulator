package com.example.stock.service.serviceInterface;

import com.example.stock.dto.request.RegisterRequest;
import com.example.stock.dto.request.UserRequest;
import com.example.stock.dto.response.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse create(UserRequest userRequest);
    UserResponse registerUser(RegisterRequest registerRequest);
    List<UserResponse> allUsers();
    UserResponse findByEmail(String email);
    void deleteUser(Long id);
    void addBalance(Long userId, Double amount);
}
