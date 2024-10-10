package com.progressSoft.fxdeals.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.progressSoft.fxdeals.model.Deal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc  // Enables MockMvc in Spring Boot tests
class DealControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;  // Inject MockMvc via field injection

    @Autowired
    private ObjectMapper objectMapper;  // Inject ObjectMapper via field injection

    @Test
    void shouldProcessMockDeals() throws Exception {
        // Use TypeReference to specify the correct generic type
        File mockFile = new File("src/test/resources/mock-deals.json");
        List<Deal> mockDeals = objectMapper.readValue(mockFile, new TypeReference<>() {});

        // Perform the POST request using MockMvc
        mockMvc.perform(post("/deals/batch")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(mockDeals)))
                .andExpect(status().isOk());
    }
}