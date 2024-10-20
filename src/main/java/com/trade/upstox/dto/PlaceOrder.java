package com.trade.upstox.dto;

import com.trade.upstox.service.UpstoxOrderService.OrderStatus;

import jakarta.persistence.Id;
import lombok.Data;

@Data
public class PlaceOrder {

	@Id
	private String orderId;
	private int quantity;
    private String product;
    private String validity = "DAY";
    private double price;
    private String tag;
    private String instrumentToken;
    private String orderType;
    private String transactionType;
    private int disclosedQuantity;
    private double triggerPrice;
    private boolean isAmo;
    private OrderStatus orderStatus;
}
