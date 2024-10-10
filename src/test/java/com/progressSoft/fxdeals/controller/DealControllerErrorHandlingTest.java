package com.progressSoft.fxdeals.controller;

import com.progressSoft.fxdeals.exception.ValidationException;
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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class DealControllerErrorHandlingTest {

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
    void shouldHandleValidationException() {
        Deal invalidDeal = createInvalidDeal();

        // Simulate a validation exception
        doThrow(new ValidationException("Invalid Currency")).when(dealValidationService).validateDeal(invalidDeal);

        ResponseEntity<String> response = dealController.addDeals(Collections.singletonList(invalidDeal));

        verify(dealRepository, never()).save(any(Deal.class));  // Ensure that the deal is not saved
        assertTrue(response.getBody().contains("Error processing Deal with UniqueId 123: Invalid Currency"));
    }

    private Deal createInvalidDeal() {
        Deal deal = new Deal();
        deal.setUniqueId("123");
        deal.setFromCurrency("USD");
        deal.setToCurrency("INVALID");  // Invalid currency code
        deal.setDealAmount(BigDecimal.valueOf(1000.00));
        deal.setDealTimestamp(LocalDateTime.now());
        return deal;
    }
}