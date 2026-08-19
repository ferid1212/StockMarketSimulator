package com.example.stock.repository;

import com.example.stock.entity.Product;
import com.example.stock.enums.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> getByName(String name);

    List<Product> getByCategory(Category category);

    @Query("SELECT p FROM Product p WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:category IS NULL OR p.category = :category)")
    List<Product> searchProducts(@Param("keyword") String keyword, @Param("category") Category category);
}
