package com.example.stock.controller;


import com.example.stock.entity.BasketItem;
import com.example.stock.service.serviceInterface.BasketService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/baskets")
public class BasketController {

    private final BasketService basketService;

    @PostMapping
    public ResponseEntity<?> addProductToBasket(@RequestParam Long userId,@RequestParam Long productId,@RequestParam Integer quantity){
        basketService.addProductToBasket(userId,productId,quantity);
        return ResponseEntity.status(HttpStatus.OK).body("Product added.");

    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<BasketItem>> getAllBasketProducts(@PathVariable Long userId) {
        List<BasketItem> basketItems = basketService.getAllBasketProducts(userId);
        return ResponseEntity.ok(basketItems);
    }


    @DeleteMapping("/payment/{userId}")
    public ResponseEntity<?> payment(@PathVariable Long userId){
        basketService.payment(userId);
        return ResponseEntity.status(HttpStatus.OK).body("Payment successful");
    }

    @DeleteMapping("/{userId}/items/{productId}")
    public ResponseEntity<String> deleteBasketItem(@PathVariable Long userId, @PathVariable Long productId) {
        basketService.deleteBasketItem(userId, productId);
        return ResponseEntity.ok("Product removed from basket successfully!");
    }




}
