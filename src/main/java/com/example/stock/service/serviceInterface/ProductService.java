package com.example.stock.service.serviceInterface;

import com.example.stock.dto.request.ProductRequest;
import com.example.stock.dto.response.ProductResponse;
import com.example.stock.enums.Category;

import java.util.List;

public interface ProductService {

    ProductResponse create(ProductRequest productRequest);

    void delete(Long id);

    void update(Long id,ProductRequest productRequest);

    ProductResponse getById(Long id);

    ProductResponse getByName(String name);

    List<ProductResponse> getByCategories(Category category);

    List<ProductResponse> getAll();

    List<ProductResponse> search(String keyword, String categoryStr);
}
