package com.example.stock.service.impl;

import com.example.stock.dto.response.ProductResponse;
import com.example.stock.entity.Basket;
import com.example.stock.entity.BasketItem;
import com.example.stock.entity.Product;
import com.example.stock.entity.User;
import com.example.stock.repository.BasketItemRepository;
import com.example.stock.repository.BasketRepository;
import com.example.stock.repository.ProductRepository;
import com.example.stock.repository.UserRepository;
import com.example.stock.service.serviceInterface.BasketService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.control.MappingControl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class BasketServiceImpl implements BasketService {

    private final BasketRepository basketRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final BasketItemRepository basketItemRepository;


    public void addProductToBasket(Long userId,Long productId,Integer quantity){

        User user=userRepository.findById(userId).orElseThrow(()->new RuntimeException("User not found!"));

        Basket basket=basketRepository.findByUserId(userId)
                .orElseGet(()->{
                    Basket newBasket=new Basket();
                    newBasket.setUser(user);
                    return basketRepository.save(newBasket);
                });

        Product product=productRepository.findById(productId).orElseThrow(()->new RuntimeException("Product not found!"));

        if(product.getStock()<quantity){
            throw new RuntimeException("Not enough stock available!");
        }


        //mehsul varmi ya yoxmu?
        Optional<BasketItem> existingItem = basketItemRepository.findByBasketIdAndProductId(basket.getId(), productId);

        if (existingItem.isPresent()) {
            BasketItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {

            BasketItem newItem = new BasketItem();
            newItem.setBasket(basket);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);

            basket.getItems().add(newItem);
        }


        basketRepository.save(basket);
    }


    public List<BasketItem> getAllBasketProducts(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        Basket basket = user.getBasket();
        if (basket == null || basket.getItems() == null) {
            return Collections.emptyList();
        }

        return basket.getItems();

    }

    @Override
    public void payment(Long userId) {
        User user=userRepository.findById(userId).orElseThrow(()->new RuntimeException("User not found!"));

        Basket basket = basketRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Basket not found!"));

        List<BasketItem> items = basket.getItems();
        if (items == null || items.isEmpty()) {
            throw new RuntimeException("Basket is empty!");
        }

        Double totalAmount=items.stream()
                .mapToDouble(item->item.getProduct().getPrice()*item.getQuantity())
                .sum();


        if (user.getBalance() < totalAmount) {
            throw new RuntimeException("Insufficient balance!");
        }

        // 3. Məhsul stoklarının yoxlanılması və yenilənməsi
        for (BasketItem item : items) {
            Product product = item.getProduct();

            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("Not enough stock for product: " + product.getName());
            }

            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
        }

        // 4. Balansdan məbləğin çıxılması
        user.setBalance(user.getBalance() - totalAmount);
        userRepository.save(user);

        // 5. Səbətin təmizlənməsi
        basket.getItems().clear(); // Səbətin daxilindəki elementləri silirik
        basketRepository.save(basket);




    }

    @Override
    public void deleteBasketItem(Long userId,Long productId) {

        User user=userRepository.findById(userId).orElseThrow(()->new RuntimeException("User not found!"));

        Basket basket = basketRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Basket not found!"));

        BasketItem itemToDelete = basketItemRepository.findByBasketIdAndProductId(basket.getId(), productId)
                .orElseThrow(() -> new RuntimeException("Product not found in basket!"));

        basket.getItems().remove(itemToDelete);

        basketItemRepository.delete(itemToDelete);

    }


}



