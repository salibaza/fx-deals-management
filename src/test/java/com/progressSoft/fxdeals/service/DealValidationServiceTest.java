package com.progressSoft.fxdeals.service;

import com.progressSoft.fxdeals.exception.ValidationException;
import com.progressSoft.fxdeals.model.Deal;
import com.progressSoft.fxdeals.repository.DealRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class DealValidationServiceTest {

    private DealValidationService dealValidationService;
    private DealRepository dealRepository;

    @BeforeEach
    void setUp() {
        dealRepository = Mockito.mock(DealRepository.class);
        dealValidationService = new DealValidationService(dealRepository);
    }

    @Test
    void shouldPassValidationForValidDeal() {
        Deal validDeal = createValidDeal();

        // Simulate that there is no existing deal with the same unique ID
        Mockito.when(dealRepository.existsByUniqueId(validDeal.getUniqueId())).thenReturn(false);

        // No exception should be thrown
        assertDoesNotThrow(() -> dealValidationService.validateDeal(validDeal));
    }

    @Test
    void shouldThrowExceptionForMissingUniqueId() {
        Deal invalidDeal = createValidDeal();
        invalidDeal.setUniqueId(null);  // Set invalid unique ID

        ValidationException exception = assertThrows(ValidationException.class, () -> dealValidationService.validateDeal(invalidDeal));

        assertEquals("Deal Unique ID is required.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForDuplicateUniqueId() {
        Deal validDeal = createValidDeal();

        // Simulate that a deal with the same unique ID already exists
        Mockito.when(dealRepository.existsByUniqueId(validDeal.getUniqueId())).thenReturn(true);

        ValidationException exception = assertThrows(ValidationException.class, () -> dealValidationService.validateDeal(validDeal));

        assertEquals("A deal with this unique ID already exists.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForInvalidFromCurrencyCodeFormat() {
        Deal invalidDeal = createValidDeal();
        invalidDeal.setFromCurrency("US");  // Invalid currency code (should be 3 letters)

        ValidationException exception = assertThrows(ValidationException.class, () -> dealValidationService.validateDeal(invalidDeal));

        assertEquals("Invalid From Currency format. Must be a 3-letter ISO code.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForInvalidToCurrencyCodeFormat() {
        Deal invalidDeal = createValidDeal();
        invalidDeal.setToCurrency("EURA");  // Invalid currency code (should be exactly 3 letters)

        ValidationException exception = assertThrows(ValidationException.class, () -> dealValidationService.validateDeal(invalidDeal));

        assertEquals("Invalid To Currency format. Must be a 3-letter ISO code.", exception.getMessage());
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