package com.example.stock.repository;

import com.example.stock.entity.Basket;
import com.example.stock.entity.Favorite;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite,Long> {


    boolean existsByUserIdAndProductId(Long userId, Long productId);

    Optional<Favorite> findByUserIdAndProductId(Long userId, Long productId);

    List<Favorite> findByUserId(Long userId);

    @Transactional
    void deleteByUserIdAndProductId(Long userId, Long productId);
}
