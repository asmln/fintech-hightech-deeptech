package com.github.asmln.fintech_app.tx_processor.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.asmln.fintech_app.domain.TransactionType;
import com.github.asmln.fintech_app.tx_processor.config.AppConfig;
import com.github.asmln.fintech_app.tx_processor.dto.TransactionRequest;
import com.github.asmln.fintech_app.tx_processor.dto.TransactionResponse;
import com.github.asmln.fintech_app.tx_processor.dto.TransactionStatus;
import com.github.asmln.fintech_app.tx_processor.service.TransactionService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TransactionController.class)
@Import(AppConfig.class)
public class TransactionJsonSerializationIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private TransactionService transactionService;

    @Test
    void shouldHideDuplicateFieldInJsonWhenItIsFalse() throws Exception {
        var userId = UUID.randomUUID();
        TransactionRequest request =
                new TransactionRequest(userId, UUID.randomUUID(), new BigDecimal("100.00"), TransactionType.DEPOSIT);
        var response =
                new TransactionResponse(TransactionStatus.ACCEPTED, "Ok", UUID.randomUUID(), userId, Instant.now());
        when(transactionService.saveTransaction(any())).thenReturn(response);
        mockMvc
            .perform(post("/api/v1/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request))
                .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isAccepted())
            .andExpect(jsonPath("$.userId").value(userId.toString()))
            .andExpect(jsonPath("$.status").value(TransactionStatus.ACCEPTED.name()))
            .andExpect(jsonPath("$.duplicate").doesNotExist());
    }
}
