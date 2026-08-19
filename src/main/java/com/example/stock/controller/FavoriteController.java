package com.example.stock.controller;


import com.example.stock.dto.response.ProductResponse;
import com.example.stock.service.serviceInterface.FavoriteService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping
    public ResponseEntity<?> addProductToFavorite(@RequestParam Long userId, @RequestParam Long productId){
        favoriteService.addProductToFavorite(userId,productId);
        return ResponseEntity.status(HttpStatus.CREATED).body("Product add to Favorite");

    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<ProductResponse>> getAllFavoriteProducts(@PathVariable Long userId) {
        List<ProductResponse> favorites = favoriteService.getAllFavoriteProducts(userId);
        return ResponseEntity.status(HttpStatus.OK).body(favorites);
    }


    @DeleteMapping("/{userId}/items/{productId}")
    public ResponseEntity<?> delete(@PathVariable Long userId,@PathVariable Long productId){
        favoriteService.delete(userId,productId);
        return ResponseEntity.status(HttpStatus.OK).body("Favorite item deleted!");

    }


}
