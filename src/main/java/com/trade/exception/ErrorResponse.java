package com.trade.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorResponse {
    private String status;
    private List<ErrorDetail> errors;
}

