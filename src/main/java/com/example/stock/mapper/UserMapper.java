package com.example.stock.mapper;


import com.example.stock.dto.request.UserRequest;
import com.example.stock.dto.response.ProductResponse;
import com.example.stock.dto.response.UserResponse;
import com.example.stock.entity.Product;
import com.example.stock.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserRequest userRequest);
    UserResponse toResponse(User entity);

    List<UserResponse> toResponseList(List<User> entities);

}
