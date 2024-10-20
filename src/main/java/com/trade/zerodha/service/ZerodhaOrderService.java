package com.trade.zerodha.service;

import java.util.List;

import com.trade.dto.HistoricalQuotes;
import org.springframework.stereotype.Service;

import com.trade.dto.LtpQuotes;
import com.trade.dto.OrderDto;
import com.trade.entity.OrderBook;
import com.trade.entity.Positions;
import com.trade.response.OrderResponse;
import com.trade.service.OrderService;
import com.upstox.ApiException;

@Service("ZerodhaOrder")
public class ZerodhaOrderService implements OrderService{

	@Override
	public OrderResponse placeOrder(String accessToken, String userId, OrderDto orderDto) throws ApiException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OrderResponse modifyOrder(String accessToken, String userId, OrderDto orderDto) throws ApiException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OrderResponse cancelOrder(String accessToken, String userId, String orderId) throws ApiException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OrderBook orderDetails(String accessToken, String userid, String orderId) throws ApiException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Positions> getPositions(String accessToken, String userId) throws ApiException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<LtpQuotes> getLtpQuotes(String accessToken, String symbol)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<HistoricalQuotes> getHistoricalQuotes(String symbol, String interval, String toDate,
													  String fromDate) throws ApiException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LtpQuotes getOneLtpQuotes(String accessToken, String symbol) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
