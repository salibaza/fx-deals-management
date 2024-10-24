package com.progressSoft.fxdeals.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.progressSoft.fxdeals.model.Deal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional  // This ensures each test is run in a transaction that is rolled back after the test
class DealControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldProcessValidDealsSuccessfully() throws Exception {
        // Load valid mock deals (all valid) from JSON file
        File mockFile = new File("src/test/resources/mock-valid-deals.json");
        List<Deal> mockDeals = objectMapper.readValue(mockFile, new TypeReference<>() {});

        // Perform POST request and expect success
        mockMvc.perform(post("/api/deals")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(mockDeals)))
                .andExpect(status().isCreated())  // Expecting 201 Created for success
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Deal with UniqueId deal123 processed successfully.")))  // Ensure success for deal123
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Deal with UniqueId deal124 processed successfully.")))  // Ensure success for deal124
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Deal with UniqueId deal125 processed successfully.")));  // Ensure success for deal125
    }

    @Test
    void shouldReturnBadRequestForInvalidCurrency() throws Exception {
        // Load mock deals including one with invalid currency from JSON file
        File mockFile = new File("src/test/resources/mock-deals-test.json");
        List<Deal> mockDeals = objectMapper.readValue(mockFile, new TypeReference<>() {});

        // Perform POST request and expect 400 Bad Request due to invalid currency in deal126
        mockMvc.perform(post("/api/deals")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(mockDeals)))
                .andExpect(status().isBadRequest())  // Expecting 400 Bad Request for invalid currency
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Deal with UniqueId deal123 processed successfully.")))  // Ensure success for deal123
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Deal with UniqueId deal124 processed successfully.")))  // Ensure success for deal124
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Error processing Deal with UniqueId deal125: Invalid To Currency")))  // Ensure error message for invalid deal126
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Deal with UniqueId deal126 processed successfully.")));  // Ensure success for deal125
    }

    @Test
    void shouldReturnBadRequestForMissingFields() throws Exception {
        // Manually create an invalid deal (missing UniqueId, Amount, and Timestamp)
        Deal invalidDeal = new Deal();
        invalidDeal.setUniqueId("");  // Invalid UniqueId
        invalidDeal.setFromCurrency("USD");
        invalidDeal.setToCurrency("EUR");
        invalidDeal.setDealAmount(null);  // Invalid amount
        invalidDeal.setDealTimestamp(null);  // Invalid timestamp

        List<Deal> invalidDeals = List.of(invalidDeal);

        // Perform POST request and verify bad request response
        mockMvc.perform(post("/api/deals")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidDeals)))
                .andExpect(status().isBadRequest())  // Expecting 400 Bad Request
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Deal Unique ID is required")))  // Specific error message
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Deal Amount is required")));    // Another error message
    }

    @Test
    void shouldReturnBadRequestForDuplicateUniqueId() throws Exception {
        // Manually create deals where one has a duplicate UniqueId
        Deal deal1 = new Deal();
        deal1.setUniqueId("deal123");
        deal1.setFromCurrency("USD");
        deal1.setToCurrency("EUR");
        deal1.setDealAmount(BigDecimal.valueOf(1000.50));
        deal1.setDealTimestamp(LocalDateTime.now());

        Deal duplicateDeal = new Deal();
        duplicateDeal.setUniqueId("deal123");  // Duplicate UniqueId
        duplicateDeal.setFromCurrency("USD");
        duplicateDeal.setToCurrency("GBP");
        duplicateDeal.setDealAmount(BigDecimal.valueOf(2000.00));
        duplicateDeal.setDealTimestamp(LocalDateTime.now());

        List<Deal> duplicateDeals = List.of(deal1, duplicateDeal);

        // Perform POST request and verify bad request response due to duplicate UniqueId
        mockMvc.perform(post("/api/deals")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(duplicateDeals)))
                .andExpect(status().isBadRequest())  // Expecting 400 Bad Request due to duplicate UniqueId
                .andExpect(content().string(org.hamcrest.Matchers.containsString("A deal with this unique ID already exists")));  // Ensure specific duplicate error message
    }
}