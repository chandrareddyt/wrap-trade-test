package com.trade.exception;

import com.trade.response.OrderResponse;

public class MarketException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1514267858198186389L;

	public MarketException(String message) {
		super(message);
	}

	public MarketException(OrderResponse message) {
		super();
	}

}
