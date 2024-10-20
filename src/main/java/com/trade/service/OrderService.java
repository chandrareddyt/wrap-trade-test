package com.trade.service;

import java.util.List;

import com.trade.dto.HistoricalQuotes;
import com.trade.dto.LtpQuotes;
import com.trade.dto.OrderDto;
import com.trade.entity.OrderBook;
import com.trade.entity.Positions;
import com.trade.response.OrderResponse;

public interface OrderService {

	public OrderResponse placeOrder(String accessToken, String userId, OrderDto orderDto) throws Exception;

	public OrderResponse modifyOrder(String accessToken, String userId, OrderDto orderDto) throws Exception;

	public OrderResponse cancelOrder(String accessToken, String orderId, String orderId2) throws Exception;

	public OrderBook orderDetails(String accessToken, String userId, String orderId) throws Exception;

	public List<Positions> getPositions(String accessToken, String userId) throws Exception;

	public List<LtpQuotes> getLtpQuotes(String accessToken, String symbol) throws Exception;
	
	public LtpQuotes getOneLtpQuotes(String accessToken, String symbol) throws Exception;

	public List<HistoricalQuotes> getHistoricalQuotes(String symbol, String interval, String toDate,
													  String fromDate) throws Exception;

}
