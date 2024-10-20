package com.trade.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorDetail {
    private String errorCode;
    private String message;
    private String propertyPath;
    private String invalidValue;
    private String error_code;
    private String property_path;
    private String invalid_value;
}
