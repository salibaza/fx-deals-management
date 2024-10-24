package com.progressSoft.fxdeals.service;

import com.progressSoft.fxdeals.exception.DealValidationException;
import com.progressSoft.fxdeals.model.Deal;
import com.progressSoft.fxdeals.repository.DealRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DealProcessingService {

    private final DealRepository dealRepository;
    private final DealValidationService dealValidationService;
    private static final Logger logger = LoggerFactory.getLogger(DealProcessingService.class);

    public DealProcessingService(DealRepository dealRepository, DealValidationService dealValidationService) {
        this.dealRepository = dealRepository;
        this.dealValidationService = dealValidationService;
    }

    public void processDeal(Deal deal) throws DealValidationException {
        logger.info("Processing deal with unique ID: {}", deal.getUniqueId());

        // First validate the deal without any database interaction
        dealValidationService.validateDeal(deal);

        // Check for duplicates after validation: Ensure that the deal does not already exist
        if (dealRepository.existsByUniqueId(deal.getUniqueId())) {
            throw new DealValidationException("A deal with this unique ID already exists.");
        }

        // If no validation errors and no duplicates, save the deal
        dealRepository.save(deal);
    }

    public List<Deal> getAllDeals() {
        return dealRepository.findAll();
    }
}