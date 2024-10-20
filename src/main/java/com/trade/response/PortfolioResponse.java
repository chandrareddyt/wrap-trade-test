package com.trade.response;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PortfolioResponse {

    public enum Status {
        SUCCESS, FAILURE
    }

    private Status status;
    private Optional<ApiError> error = Optional.empty();
    private Optional<OrderData> data = Optional.empty();

    @Data
    public static class ApiError {
        private String errorCode;
        private String message;
    }

    @Data
    public static class OrderData {
        private String message;
        private Long orderId;
    }

    public void setSuccess(Long orderId, String message) {
        this.status = Status.SUCCESS;
        this.data = Optional.of(new OrderData());
        this.data.get().setMessage(message);
        this.data.get().setOrderId(orderId);
    }

    public void setFailure(String errorCode, String message) {
        this.status = Status.FAILURE;
        this.error = Optional.of(new ApiError());
        this.error.get().setErrorCode(errorCode);
        this.error.get().setMessage(message);
    }

    public PortfolioResponse(Status status, String codeOrId, String message) {
        if (status == Status.SUCCESS) {
            setSuccess(Long.parseLong(codeOrId), message);
        } else {
            setFailure(codeOrId, message);
        }
    }
}