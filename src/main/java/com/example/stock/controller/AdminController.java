package com.example.stock.controller;

import com.example.stock.dto.request.ProductRequest;
import com.example.stock.dto.request.UserRequest;
import com.example.stock.dto.response.ProductResponse;
import com.example.stock.dto.response.UserResponse;
import com.example.stock.enums.Category;
import com.example.stock.service.serviceInterface.ProductService;
import com.example.stock.service.serviceInterface.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final ProductService productService;

    @GetMapping
    public String adminDashboard(@RequestParam(value = "search", required = false) String search,
                                 @RequestParam(value = "category", required = false) String category,
                                 Model model) {
        List<UserResponse> users = userService.allUsers();
        List<ProductResponse> products;
        try {
            products = productService.search(search, category);
        } catch (Exception e) {
            products = Collections.emptyList();
        }

        model.addAttribute("users", users);
        model.addAttribute("products", products);
        model.addAttribute("categories", Category.values());
        model.addAttribute("searchQuery", search != null ? search : "");
        model.addAttribute("selectedCategory", category != null ? category : "");
        model.addAttribute("userCount", users.size());
        model.addAttribute("productCount", products.size());
        model.addAttribute("newUserRequest", new UserRequest());
        model.addAttribute("newProductRequest", new ProductRequest());

        return "admin/dashboard";
    }

    @PostMapping("/users/create")
    public String createUser(@ModelAttribute("newUserRequest") UserRequest userRequest) {
        try {
            userService.create(userRequest);
        } catch (Exception ignored) {}
        return "redirect:/admin?userCreated=true";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        try {
            userService.deleteUser(id);
        } catch (Exception ignored) {}
        return "redirect:/admin?userDeleted=true";
    }

    @PostMapping("/products/create")
    public String createProduct(@ModelAttribute("newProductRequest") ProductRequest productRequest) {
        try {
            productService.create(productRequest);
        } catch (Exception ignored) {}
        return "redirect:/admin?productCreated=true";
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id) {
        try {
            productService.delete(id);
        } catch (Exception ignored) {}
        return "redirect:/admin?productDeleted=true";
    }

    @PostMapping("/products/update/{id}")
    public String updateProduct(@PathVariable("id") Long id, @ModelAttribute("productRequest") ProductRequest productRequest) {
        try {
            productService.update(id, productRequest);
        } catch (Exception ignored) {}
        return "redirect:/admin?productUpdated=true";
    }
}
