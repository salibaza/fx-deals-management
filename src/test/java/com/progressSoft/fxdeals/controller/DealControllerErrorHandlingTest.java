package com.progressSoft.fxdeals.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.progressSoft.fxdeals.model.Deal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DealControllerErrorHandlingTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Deal invalidDeal;

    @BeforeEach
    void setUp() {
        invalidDeal = createInvalidDeal();
    }

    @Test
    void shouldHandleValidationException() throws Exception {
        // Convert the invalid deal to JSON
        String invalidDealJson = objectMapper.writeValueAsString(Collections.singletonList(invalidDeal));

        // Perform the POST request using MockMvc
        mockMvc.perform(post("/api/deals")
                        .contentType("application/json")
                        .content(invalidDealJson))
                .andExpect(status().isBadRequest())  // Expecting 400 Bad Request
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Invalid To Currency")));  // Check specific error message
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