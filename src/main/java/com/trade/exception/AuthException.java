package com.trade.exception;

import com.trade.response.OrderResponse;

public class AuthException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1514267858198186389L;

	public AuthException(String message) {
		super(message);
	}

	public AuthException(OrderResponse message) {
		super();
	}

}
