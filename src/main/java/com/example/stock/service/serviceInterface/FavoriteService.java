package com.example.stock.service.serviceInterface;

import com.example.stock.dto.response.ProductResponse;

import java.util.List;

public interface FavoriteService {

    void addProductToFavorite(Long userId,Long productId);

    List<ProductResponse> getAllFavoriteProducts(Long userId);


    void delete(Long userId,Long productId);
}
