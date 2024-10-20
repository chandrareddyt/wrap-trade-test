package com.trade.exception;

import com.trade.response.OrderResponse;

public class OrderException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1514267858198186389L;

	public OrderException(String message) {
		super(message);
	}

	public OrderException(OrderResponse message) {
		super();
	}

}
