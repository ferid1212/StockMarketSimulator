package com.example.stock.controller;


import com.example.stock.dto.request.ProductRequest;
import com.example.stock.dto.response.ProductResponse;
import com.example.stock.enums.Category;
import com.example.stock.service.serviceInterface.ProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;


    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest productRequest){
        ProductResponse productResponse=productService.create(productRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(productResponse);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
         productService.delete(id);
         return ResponseEntity.status(HttpStatus.OK).body("Product deleted.");

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,@Valid @RequestBody ProductRequest productRequest){
        productService.update(id,productRequest);
        return ResponseEntity.status(HttpStatus.OK).body("Product updated");
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAll(){
        List<ProductResponse> productResponses=productService.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(productResponses);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable Long id){

        ProductResponse productResponse=productService.getById(id);
        return ResponseEntity.status(HttpStatus.OK).body(productResponse);

    }

    @GetMapping("/search/{name}")
    public ResponseEntity<ProductResponse> getByName(@PathVariable String name){
        ProductResponse productResponse=productService.getByName(name);
        return ResponseEntity.status(HttpStatus.OK).body(productResponse);

    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductResponse>> getByCategory(@PathVariable Category category){
        List<ProductResponse> productResponses=productService.getByCategories(category);

        return ResponseEntity.status(HttpStatus.OK).body(productResponses);

    }


}
