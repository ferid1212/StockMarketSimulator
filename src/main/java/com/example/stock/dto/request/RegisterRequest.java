package com.example.stock.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterRequest {

    @NotBlank(message = "Ad boş ola bilməz")
    String name;

    @NotBlank(message = "Soyad boş ola bilməz")
    String surname;

    @Min(value = 18, message = "Yaş ən azı 18 olmalıdır")
    int age;

    @NotBlank(message = "Telefon nömrəsi boş ola bilməz")
    String phone;

    @Email(message = "Düzgün email ünvanı daxil edin")
    @NotBlank(message = "Email boş ola bilməz")
    String email;

    @NotBlank(message = "Şifrə boş ola bilməz")
    @Size(min = 6, message = "Şifrə ən azı 6 simvol olmalıdır")
    String password;
}
