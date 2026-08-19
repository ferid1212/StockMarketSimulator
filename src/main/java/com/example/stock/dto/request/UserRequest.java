package com.example.stock.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRequest {

    @NotBlank(message = "Name cannot be empty")
    String name;

    @NotBlank(message = "Surname cannot be empty")
    String surname;

    @Min(value = 18, message = "Age must be at least 18")
    int age;

    @NotBlank(message = "Phone cannot be empty")
    String phone;

    @Email(message = "Email format is invalid")
    @NotBlank(message = "Email cannot be empty")
    String email;

    String password;

    String role;

    @PositiveOrZero(message = "Balance cannot be negative")
    Double balance;
}
