package com.example.stock.service.serviceInterface;

import com.example.stock.entity.BasketItem;

import java.util.List;

public interface BasketService {

    void addProductToBasket(Long userId,Long productId,Integer quantity);

    List<BasketItem> getAllBasketProducts(Long userId);

    void payment(Long userId);

    void deleteBasketItem(Long userId,Long productId);
}
