package com.example.stock.controller;

import com.example.stock.dto.request.RegisterRequest;
import com.example.stock.dto.response.ProductResponse;
import com.example.stock.dto.response.UserResponse;
import com.example.stock.entity.BasketItem;
import com.example.stock.entity.User;
import com.example.stock.enums.Category;
import com.example.stock.repository.UserRepository;
import com.example.stock.service.serviceInterface.BasketService;
import com.example.stock.service.serviceInterface.FavoriteService;
import com.example.stock.service.serviceInterface.ProductService;
import com.example.stock.service.serviceInterface.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final ProductService productService;
    private final BasketService basketService;
    private final FavoriteService favoriteService;
    private final UserRepository userRepository;

    @GetMapping("/")
    public String index(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                return "redirect:/admin";
            }
            return "redirect:/dashboard";
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String handleRegister(@Valid @ModelAttribute("registerRequest") RegisterRequest registerRequest,
                                 BindingResult bindingResult,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        try {
            userService.registerUser(registerRequest);
            return "redirect:/login?registered=true";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication,
                            @RequestParam(value = "search", required = false) String search,
                            @RequestParam(value = "category", required = false) String category,
                            Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            try {
                User userEntity = userRepository.findByEmail(authentication.getName()).orElse(null);
                if (userEntity != null) {
                    UserResponse currentUser = userService.findByEmail(userEntity.getEmail());
                    model.addAttribute("user", currentUser);
                    model.addAttribute("userId", userEntity.getId());

                    // Products catalog with search and category filtering
                    List<ProductResponse> products;
                    try {
                        products = productService.search(search, category);
                    } catch (Exception e) {
                        products = Collections.emptyList();
                    }
                    model.addAttribute("products", products);
                    model.addAttribute("categories", Category.values());
                    model.addAttribute("searchQuery", search != null ? search : "");
                    model.addAttribute("selectedCategory", category != null ? category : "");

                    // Basket Items
                    List<BasketItem> basketItems;
                    try {
                        basketItems = basketService.getAllBasketProducts(userEntity.getId());
                    } catch (Exception e) {
                        basketItems = Collections.emptyList();
                    }
                    model.addAttribute("basketItems", basketItems);

                    Double basketTotal = basketItems.stream()
                            .mapToDouble(item -> (item.getProduct() != null ? item.getProduct().getPrice() : 0.0) * item.getQuantity())
                            .sum();
                    model.addAttribute("basketTotal", basketTotal);

                    // Favorite items
                    List<ProductResponse> favorites;
                    try {
                        favorites = favoriteService.getAllFavoriteProducts(userEntity.getId());
                    } catch (Exception e) {
                        favorites = Collections.emptyList();
                    }
                    model.addAttribute("favorites", favorites);
                }
            } catch (Exception ignored) {}
        }
        return "dashboard";
    }

    @PostMapping("/basket/add")
    public String addToBasket(Authentication authentication,
                              @RequestParam("productId") Long productId,
                              @RequestParam(value = "quantity", defaultValue = "1") Integer quantity) {
        if (authentication != null && authentication.isAuthenticated()) {
            User userEntity = userRepository.findByEmail(authentication.getName()).orElse(null);
            if (userEntity != null) {
                try {
                    basketService.addProductToBasket(userEntity.getId(), productId, quantity);
                    return "redirect:/dashboard?basketAdded=true";
                } catch (Exception e) {
                    return "redirect:/dashboard?error=" + e.getMessage();
                }
            }
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/basket/remove/{productId}")
    public String removeFromBasket(Authentication authentication, @PathVariable("productId") Long productId) {
        if (authentication != null && authentication.isAuthenticated()) {
            User userEntity = userRepository.findByEmail(authentication.getName()).orElse(null);
            if (userEntity != null) {
                try {
                    basketService.deleteBasketItem(userEntity.getId(), productId);
                    return "redirect:/dashboard?basketRemoved=true";
                } catch (Exception e) {
                    return "redirect:/dashboard?error=" + e.getMessage();
                }
            }
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/basket/payment")
    public String processPayment(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            User userEntity = userRepository.findByEmail(authentication.getName()).orElse(null);
            if (userEntity != null) {
                try {
                    basketService.payment(userEntity.getId());
                    return "redirect:/dashboard?paid=true";
                } catch (Exception e) {
                    return "redirect:/dashboard?error=" + e.getMessage();
                }
            }
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/favorites/toggle/{productId}")
    public String toggleFavorite(Authentication authentication, @PathVariable("productId") Long productId) {
        if (authentication != null && authentication.isAuthenticated()) {
            User userEntity = userRepository.findByEmail(authentication.getName()).orElse(null);
            if (userEntity != null) {
                try {
                    List<ProductResponse> favs = favoriteService.getAllFavoriteProducts(userEntity.getId());
                    boolean isFav = favs.stream().anyMatch(p -> p.getId() != null && p.getId().equals(productId));
                    if (isFav) {
                        favoriteService.delete(userEntity.getId(), productId);
                    } else {
                        favoriteService.addProductToFavorite(userEntity.getId(), productId);
                    }
                    return "redirect:/dashboard?favUpdated=true";
                } catch (Exception e) {
                    return "redirect:/dashboard?error=" + e.getMessage();
                }
            }
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/favorites/delete/{productId}")
    public String deleteFavorite(Authentication authentication, @PathVariable("productId") Long productId) {
        if (authentication != null && authentication.isAuthenticated()) {
            User userEntity = userRepository.findByEmail(authentication.getName()).orElse(null);
            if (userEntity != null) {
                try {
                    favoriteService.delete(userEntity.getId(), productId);
                    return "redirect:/dashboard?favUpdated=true";
                } catch (Exception e) {
                    return "redirect:/dashboard?error=" + e.getMessage();
                }
            }
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/user/add-balance")
    public String addBalance(Authentication authentication,
                             @RequestParam(value = "amount", defaultValue = "100.0") Double amount) {
        if (authentication != null && authentication.isAuthenticated()) {
            User userEntity = userRepository.findByEmail(authentication.getName()).orElse(null);
            if (userEntity != null) {
                try {
                    userService.addBalance(userEntity.getId(), amount);
                    return "redirect:/dashboard?balanceAdded=true";
                } catch (Exception e) {
                    return "redirect:/dashboard?error=" + e.getMessage();
                }
            }
        }
        return "redirect:/dashboard";
    }
}
