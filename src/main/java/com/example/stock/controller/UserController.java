package com.example.stock.controller;


import com.example.stock.dto.request.UserRequest;
import com.example.stock.dto.response.UserResponse;
import com.example.stock.service.serviceInterface.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest userRequest){
        UserResponse userResponse=userService.create(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);

    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAll(){
        List<UserResponse> userResponses=userService.allUsers();

        return ResponseEntity.status(HttpStatus.OK).body(userResponses);
    }
}
