package com.example.stock.mapper;


import com.example.stock.dto.request.ProductRequest;
import com.example.stock.dto.response.ProductResponse;
import com.example.stock.entity.Product;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toEntity(ProductRequest request);
    ProductResponse toResponse(Product entity);

    List<ProductResponse> toResponseList(List<Product> entities);



}
