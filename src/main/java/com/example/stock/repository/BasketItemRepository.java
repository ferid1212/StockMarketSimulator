package com.example.stock.repository;

import com.example.stock.entity.BasketItem;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface BasketItemRepository extends JpaRepository<BasketItem,Long> {

    Optional<BasketItem> findByBasketIdAndProductId(Long basketId, Long productId);

    @Modifying
    @Transactional
    @Query("DELETE FROM BasketItem b WHERE b.createdAt <= :cutoffTime")
    void deleteExpiredBasketItems(LocalDateTime cutoffTime);
}
