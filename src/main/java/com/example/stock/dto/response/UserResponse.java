package com.example.stock.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {

    Long id;
    String name;
    String surname;
    int age;
    String phone;
    String email;
    String role;
    Double balance;
}
