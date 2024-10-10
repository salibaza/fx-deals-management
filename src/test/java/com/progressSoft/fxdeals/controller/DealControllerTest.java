package com.progressSoft.fxdeals.controller;

import com.progressSoft.fxdeals.model.Deal;
import com.progressSoft.fxdeals.repository.DealRepository;
import com.progressSoft.fxdeals.service.DealValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class DealControllerTest {

    private DealController dealController;
    private DealRepository dealRepository;
    private DealValidationService dealValidationService;

    @BeforeEach
    void setUp() {
        dealRepository = Mockito.mock(DealRepository.class);
        dealValidationService = Mockito.mock(DealValidationService.class);
        dealController = new DealController(dealRepository, dealValidationService);
    }

    @Test
    void shouldSaveDealToDatabaseWhenValid() {
        Deal validDeal = createValidDeal();

        // No validation exceptions
        doNothing().when(dealValidationService).validateDeal(validDeal);

        ResponseEntity<String> response = dealController.addDeals(Collections.singletonList(validDeal));

        verify(dealRepository, times(1)).save(validDeal);
        assertEquals("Deal with UniqueId 123 processed successfully.\n", response.getBody());
    }

    // Helper method to create a valid deal
    private Deal createValidDeal() {
        Deal deal = new Deal();
        deal.setUniqueId("123");
        deal.setFromCurrency("USD");
        deal.setToCurrency("EUR");
        deal.setDealAmount(BigDecimal.valueOf(1000.00));
        deal.setDealTimestamp(LocalDateTime.now());
        return deal;
    }
}