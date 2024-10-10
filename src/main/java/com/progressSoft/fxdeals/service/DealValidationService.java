package com.progressSoft.fxdeals.service;

import com.progressSoft.fxdeals.exception.ValidationException;
import com.progressSoft.fxdeals.model.Deal;
import com.progressSoft.fxdeals.repository.DealRepository;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class DealValidationService {
    private static final Pattern CURRENCY_CODE_PATTERN = Pattern.compile("^[A-Z]{3}$"); // 3-letter ISO currency code pattern

    private final DealRepository dealRepository;

    public DealValidationService(DealRepository dealRepository) {
        this.dealRepository = dealRepository;
    }

    public void validateDeal(Deal deal) throws ValidationException {
        // Validate Mandatory Fields
        if (deal.getUniqueId() == null || deal.getUniqueId().isEmpty()) {
            throw new ValidationException("Deal Unique ID is required.");
        }
        if (deal.getFromCurrency() == null || deal.getFromCurrency().isEmpty()) {
            throw new ValidationException("From Currency is required.");
        }
        if (deal.getToCurrency() == null || deal.getToCurrency().isEmpty()) {
            throw new ValidationException("To Currency is required.");
        }
        if (deal.getDealTimestamp() == null) {
            throw new ValidationException("Deal Timestamp is required.");
        }
        if (deal.getDealAmount() == null || deal.getDealAmount().doubleValue() < 0) {
            throw new ValidationException("Deal Amount is required and must be a positive number.");
        }

        // Validate Currency Code Format
        if (!CURRENCY_CODE_PATTERN.matcher(deal.getFromCurrency()).matches()) {
            throw new ValidationException("Invalid From Currency format. Must be a 3-letter ISO code.");
        }
        if (!CURRENCY_CODE_PATTERN.matcher(deal.getToCurrency()).matches()) {
            throw new ValidationException("Invalid To Currency format. Must be a 3-letter ISO code.");
        }

        // Duplicate Check: Ensure that the deal does not already exist
        if (dealRepository.existsByUniqueId(deal.getUniqueId())) {
            throw new ValidationException("A deal with this unique ID already exists.");
        }
    }
}
