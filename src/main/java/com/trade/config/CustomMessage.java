package com.trade.config;

import lombok.Getter;

@Getter
public class CustomMessage {

    private final String message;

    public CustomMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }

}
