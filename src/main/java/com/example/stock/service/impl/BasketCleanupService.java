package com.example.stock.service.impl;


import com.example.stock.repository.BasketItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasketCleanupService {

    private final BasketItemRepository basketItemRepository;

    // Hər 1 saatdan bir avtomatik işə düşür (3600000 ms)
    @Scheduled(fixedRate = 3600000)
    public void cleanupOldBasketItems() {
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);

        basketItemRepository.deleteExpiredBasketItems(twentyFourHoursAgo);
        log.info("24 saatı keçmiş səbət məhsulları təmizləndi.");
    }
}
