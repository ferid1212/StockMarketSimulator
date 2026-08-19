package com.example.stock.service.impl;

import com.example.stock.dto.response.ProductResponse;
import com.example.stock.entity.Favorite;
import com.example.stock.entity.Product;
import com.example.stock.entity.User;
import com.example.stock.mapper.ProductMapper;
import com.example.stock.repository.FavoriteRepository;
import com.example.stock.repository.ProductRepository;
import com.example.stock.repository.UserRepository;
import com.example.stock.service.serviceInterface.FavoriteService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public void addProductToFavorite(Long userId, Long productId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found!"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found!"));

        if (favoriteRepository.existsByUserIdAndProductId(userId, productId)) {
            return;
        }

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setProduct(product);

        favoriteRepository.save(favorite);
    }

    @Override
    public List<ProductResponse> getAllFavoriteProducts(Long userId) {
        List<Favorite> favorites = favoriteRepository.findByUserId(userId);
        return favorites.stream()
                .map(favorite -> productMapper.toResponse(favorite.getProduct()))
                .toList();
    }

    @Override
    public void delete(Long userId, Long productId) {
        Optional<Favorite> favoriteOpt = favoriteRepository.findByUserIdAndProductId(userId, productId);
        if (favoriteOpt.isPresent()) {
            favoriteRepository.delete(favoriteOpt.get());
            favoriteRepository.flush();
        } else {
            favoriteRepository.deleteByUserIdAndProductId(userId, productId);
        }
    }
}
