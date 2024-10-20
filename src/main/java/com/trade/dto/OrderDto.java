package com.trade.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrderDto extends BaseDto{

	private String orderId;
//    private String broker;//broker name upstox, zerodha
	@NotNull(message = "Symbol is required")
    private String instrumentToken;// scrip name
	@NotNull(message = "Order Type is required(M/L) M-Market, L-LIMIT")
    private String orderType;//market or limit
	@NotNull(message = "Product is required(I/D) I-Intraday, D-Delivery")
    private String product;//intraday or delivery
	@NotNull(message = "Quantity is required")
    private int quantity;//qunatity
	@NotNull(message = "Price is required")
    private float price;//modify to enter price
	@NotNull(message = "Transaction Type is required(BUY/SELL)")
    private String transactionType;//buy or sell
	@NotNull(message = "Exchange is required(NSE or BSE)")
    private String exchange;
	
}
