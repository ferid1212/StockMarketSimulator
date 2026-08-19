package com.example.stock.dto.response;

import com.example.stock.enums.Category;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;


@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {

    @NotNull(message = "ID is required")
    @Positive(message = "ID must be positive")
    Long id;
    @NotBlank(message = "Product name is required")
    String name;
    @NotNull(message = "Price is required")
    @Digits(integer = 5, fraction = 2, message = "Price must have at most 5 digits and 2 decimal places")
    Double price;
    Category category;
    @NotNull(message = "Stock is required")
    int stock;
    String imgURL;
}
