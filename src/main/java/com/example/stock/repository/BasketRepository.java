package com.example.stock.repository;

import com.example.stock.entity.Basket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BasketRepository extends JpaRepository<Basket,Long> {

    Optional<Basket> findByUserId(Long userId);

}
