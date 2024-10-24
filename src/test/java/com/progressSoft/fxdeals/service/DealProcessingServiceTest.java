package com.progressSoft.fxdeals.service;

import com.progressSoft.fxdeals.exception.DealValidationException;
import com.progressSoft.fxdeals.model.Deal;
import com.progressSoft.fxdeals.repository.DealRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DealProcessingServiceTest {

    private DealProcessingService dealProcessingService;
    private DealRepository dealRepository;
    private DealValidationService dealValidationService;

    @BeforeEach
    void setUp() {
        dealRepository = Mockito.mock(DealRepository.class);
        dealValidationService = Mockito.mock(DealValidationService.class);
        dealProcessingService = new DealProcessingService(dealRepository, dealValidationService);
    }

    @Test
    void shouldProcessValidDeal() {
        Deal validDeal = createValidDeal();

        // Simulate that there is no existing deal with the same unique ID
        when(dealRepository.existsByUniqueId(validDeal.getUniqueId())).thenReturn(false);

        // No validation exceptions should occur
        doNothing().when(dealValidationService).validateDeal(validDeal);

        // Call the method
        assertDoesNotThrow(() -> dealProcessingService.processDeal(validDeal));

        // Verify that the validation was called
        verify(dealValidationService).validateDeal(validDeal);

        // Verify that the deal was saved
        verify(dealRepository).save(validDeal);
    }

    @Test
    void shouldThrowExceptionForDuplicateUniqueId() {
        Deal validDeal = createValidDeal();

        // Simulate that a deal with the same unique ID already exists
        when(dealRepository.existsByUniqueId(validDeal.getUniqueId())).thenReturn(true);

        // Expect a validation exception due to duplicate deal
        DealValidationException exception = assertThrows(DealValidationException.class, () -> dealProcessingService.processDeal(validDeal));

        assertEquals("A deal with this unique ID already exists.", exception.getMessage());

        // Verify that the deal was not saved
        verify(dealRepository, never()).save(validDeal);
    }

    @Test
    void shouldThrowValidationExceptionForInvalidDeal() {
        Deal invalidDeal = createValidDeal();

        // Simulate a validation error
        doThrow(new DealValidationException("Invalid Deal")).when(dealValidationService).validateDeal(invalidDeal);

        // Expect validation exception
        DealValidationException exception = assertThrows(DealValidationException.class, () -> dealProcessingService.processDeal(invalidDeal));

        assertEquals("Invalid Deal", exception.getMessage());

        // Verify that the deal was not saved
        verify(dealRepository, never()).save(invalidDeal);
    }

    @Test
    void shouldHandleRepositorySaveFailure() {
        Deal validDeal = createValidDeal();

        // Simulate that there is no existing deal with the same unique ID
        when(dealRepository.existsByUniqueId(validDeal.getUniqueId())).thenReturn(false);

        // No validation exceptions should occur
        doNothing().when(dealValidationService).validateDeal(validDeal);

        // Simulate repository save failure (e.g., database error)
        doThrow(new RuntimeException("Database error")).when(dealRepository).save(validDeal);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> dealProcessingService.processDeal(validDeal));

        assertEquals("Database error", exception.getMessage());

        // Verify that the validation was called
        verify(dealValidationService).validateDeal(validDeal);

        // Verify that the repository attempted to save
        verify(dealRepository).save(validDeal);
    }

    @Test
    void shouldThrowExceptionForNullDeal() {
        // Expect a validation exception for null input
        assertThrows(NullPointerException.class, () -> dealProcessingService.processDeal(null));
    }

    @Test
    void shouldReturnAllDealsFromRepository() {
        // Arrange
        Deal deal = createValidDeal();
        when(dealRepository.findAll()).thenReturn(List.of(deal));

        // Act
        List<Deal> deals = dealProcessingService.getAllDeals();

        // Assert
        assertEquals(1, deals.size());
        assertEquals("123", deals.get(0).getUniqueId());
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