package com.trade.response;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AuthResponse {

    public enum Status {
        SUCCESS, FAILURE
    }

    private Status status;
    private Optional<ApiError> error = Optional.empty();
    private Optional<AuthData> data = Optional.empty();

    @Data
    public static class ApiError {
        private String errorCode;
        private String message;
    }

    @Data
    public static class AuthData {
        private String accessToken;
        private boolean hasAuthentication;
    }

    public void setSuccess(boolean hasAuthentication, String accessToken) {
        this.status = Status.SUCCESS;
        this.data = Optional.of(new AuthData());
        this.data.get().setAccessToken(accessToken);
        this.data.get().setHasAuthentication(hasAuthentication);
    }

    public void setFailure(String errorCode, String message) {
        this.status = Status.FAILURE;
        this.error = Optional.of(new ApiError());
        this.error.get().setErrorCode(errorCode);
        this.error.get().setMessage(message);
    }

    public AuthResponse(Status status, String hasAuthentication, String message) {
        if (status == Status.SUCCESS) {
            setSuccess(Boolean.parseBoolean(hasAuthentication), message);
        } else {
            setFailure(hasAuthentication, message);
        }
    }
}