package com.trade.exception;

import com.trade.response.OrderResponse;

public class PortfolioException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1514267858198186389L;

	public PortfolioException(String message) {
		super(message);
	}

	public PortfolioException(OrderResponse message) {
		super();
	}

}
