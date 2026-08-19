package com.example.stock.service.impl;

import com.example.stock.dto.request.ProductRequest;
import com.example.stock.dto.response.ProductResponse;
import com.example.stock.entity.Product;
import com.example.stock.enums.Category;
import com.example.stock.mapper.ProductMapper;
import com.example.stock.repository.ProductRepository;
import com.example.stock.service.serviceInterface.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private  final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductResponse create(ProductRequest productRequest) {
        Product product=Product.builder()
                .name(productRequest.getName())
                .price(productRequest.getPrice())
                .category(productRequest.getCategory())
                .stock(productRequest.getStock())
                .imgURL(productRequest.getImgURL())
                .build();

        Product saved=productRepository.save(product);
        return productMapper.toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        Product product=productRepository.findById(id).orElseThrow(()->new RuntimeException("Product not found!"));
        productRepository.delete(product);


    }

    @Override
    public void update(Long id, ProductRequest productRequest) {
        Product product=productRepository.findById(id).orElseThrow(()->new RuntimeException("Product not found!"));
        product.setName(productRequest.getName());
        product.setCategory(productRequest.getCategory());
        product.setPrice(productRequest.getPrice());
        product.setStock(productRequest.getStock());
        product.setImgURL(productRequest.getImgURL());

        productRepository.save(product);




    }

    @Override
    public ProductResponse getById(Long id) {
        Product product=productRepository.findById(id).orElseThrow(()->new RuntimeException("Product not found!"));

        return productMapper.toResponse(product);
    }

    @Override
    public ProductResponse getByName(String name) {
        Product product=productRepository.getByName(name).orElseThrow(()->new RuntimeException("Product not found!"));
        return productMapper.toResponse(product);
    }

    @Override
    public List<ProductResponse> getByCategories(Category category) {
        List<Product> products=productRepository.getByCategory(category);
        if (products.isEmpty()){
            throw new RuntimeException("Category not found!");
        }
        return productMapper.toResponseList(products);


    }

    @Override
    public List<ProductResponse> getAll() {
        List<Product> products=productRepository.findAll();
        if(products.isEmpty()){
            return java.util.Collections.emptyList();
        }
        return productMapper.toResponseList(products);
    }

    @Override
    public List<ProductResponse> search(String keyword, String categoryStr) {
        Category category = null;
        if (categoryStr != null && !categoryStr.isBlank()) {
            try {
                category = Category.valueOf(categoryStr);
            } catch (Exception ignored) {}
        }
        String cleanKeyword = (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;

        List<Product> products = productRepository.searchProducts(cleanKeyword, category);
        return productMapper.toResponseList(products);
    }
}
