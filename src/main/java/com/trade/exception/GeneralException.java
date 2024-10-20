package com.trade.exception;

import com.trade.response.OrderResponse;

public class GeneralException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1514267858198186389L;

	public GeneralException(String message) {
		super(message);
	}

	public GeneralException(OrderResponse message) {
		super();
	}

}
