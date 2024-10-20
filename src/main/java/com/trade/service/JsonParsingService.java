package com.trade.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trade.exception.ErrorResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class JsonParsingService {

    private final ObjectMapper objectMapper;

    public JsonParsingService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String extractErrorMessage(String jsonResponse) {
        try {
            ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, ErrorResponse.class);
            if (errorResponse.getErrors() != null && !errorResponse.getErrors().isEmpty()) {
                return errorResponse.getErrors().get(0).getMessage();
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception as needed
        }
        return "No error message found.";
    }
}

