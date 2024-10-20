package com.trade.upstox.service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.trade.dto.HistoricalQuotes;
import com.upstox.api.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.trade.dto.LtpQuotes;
import com.trade.dto.OrderDto;
import com.trade.entity.OrderBook;
import com.trade.entity.Positions;
import com.trade.exception.MarketException;
import com.trade.exception.OrderException;
import com.trade.repository.OrderBookRepo;
import com.trade.repository.PositionsRepo;
import com.trade.response.OrderResponse;
import com.trade.service.OrderService;
import com.trade.upstox.dto.PlaceOrder;
import com.upstox.ApiClient;
import com.upstox.ApiException;
import com.upstox.Configuration;
import com.upstox.auth.OAuth;

import io.swagger.client.api.HistoryApi;
import io.swagger.client.api.MarketQuoteApi;
import io.swagger.client.api.OrderApi;
import io.swagger.client.api.PortfolioApi;

@Service("UpstoxOrder")
public class UpstoxOrderService implements OrderService {

	private static final Logger logger = LoggerFactory.getLogger(UpstoxOrderService.class);

	@Autowired
	private OrderBookRepo orderRepo;

	@Autowired
	private PositionsRepo positionsRepo;

	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private UpstoxPortfolioService upstoxPortfolioService;

	public enum OrderStatus {
		ORDER_PLACED, ORDER_MODIFIED, ORDER_CANCELLED, ORDER_CLOSED_BY_USER, ORDER_CLOSED, ORDER_DETAIL_FULFILL, ORDER_SUBMITTED
	}

	@Override
	public OrderResponse placeOrder(String accessToken, String userId, OrderDto orderDto) throws Exception {
		PlaceOrder placeOrder = modelMapper.map(orderDto, PlaceOrder.class);
		PlaceOrderResponse res = placeOrderUpstox(accessToken, placeOrder);
		logger.info("OrderDto placed: {}", res);
		if (res != null) {
			List<OrderBook> list = upstoxPortfolioService.orderBook(accessToken, userId);
			OrderBook createdOrder = list.stream().filter(o -> o.getOrderId().equals(res.getData().getOrderId())).collect(Collectors.toList()).get(0);
//			OrderBook tradeOrder = modelMapper.map(createdOrder, OrderBook.class);
//			tradeOrder.setUserId(userId);
//			tradeOrder.setBroker("upstox");
//			tradeOrder.setOrderId(res.getData().getOrderId());
//			tradeOrder.setStatus(OrderStatus.ORDER_SUBMITTED.toString());
//			tradeOrder.setCreatedDate(new Date());;
//			orderRepo.save(tradeOrder);
			return new OrderResponse(
				    createdOrder.getStatus().equals("rejected") ? OrderResponse.Status.FAILURE : OrderResponse.Status.SUCCESS,
				    createdOrder.getOrderId(),
				    createdOrder.getStatus() + (createdOrder.getStatusMessage() != null ? " : " + createdOrder.getStatusMessage() : "")
				);
		}
		throw new OrderException("Failed to place order");
	}

	public PlaceOrderResponse placeOrderUpstox(String accessToken, PlaceOrder placeOrder) throws ApiException {
		ApiClient defaultClient = Configuration.getDefaultApiClient();

		OAuth OAUTH2 = (OAuth) defaultClient.getAuthentication("OAUTH2");
		OAUTH2.setAccessToken(accessToken);

		OrderApi apiInstance = new OrderApi();
		PlaceOrderRequest body = new PlaceOrderRequest();
		body.setQuantity(placeOrder.getQuantity());
		body.setProduct(PlaceOrderRequest.ProductEnum.valueOf(placeOrder.getProduct()));
		body.setValidity(PlaceOrderRequest.ValidityEnum.valueOf(placeOrder.getValidity()));
		body.setPrice((float) placeOrder.getPrice());
		body.setTag(placeOrder.getTag());
		body.setInstrumentToken(placeOrder.getInstrumentToken());
		body.orderType(PlaceOrderRequest.OrderTypeEnum.valueOf(placeOrder.getOrderType()));
		body.setTransactionType(PlaceOrderRequest.TransactionTypeEnum.valueOf(placeOrder.getTransactionType()));
		body.setDisclosedQuantity(placeOrder.getDisclosedQuantity());
		body.setTriggerPrice((float) placeOrder.getTriggerPrice());
		body.setIsAmo(false);

		String apiVersion = "2.0"; // String | API Version Header
		PlaceOrderResponse result = null;
		try {
			result = apiInstance.placeOrder(body, apiVersion);
			result.setStatus(PlaceOrderResponse.StatusEnum.SUCCESS);

		} catch (ApiException e) {
			logger.error("Exception when calling OrderApi#placeOrder {} {} {}", e.getCode(), e.getMessage(),
					e.getResponseBody());
			throw e;
		}
		return result;
	}

	@Override
	public OrderResponse modifyOrder(String accessToken, String userId, OrderDto orderDto) throws Exception {
		PlaceOrder placeOrder = modelMapper.map(orderDto, PlaceOrder.class);
		ModifyOrderResponse res = modifyOrderUpstox(accessToken, placeOrder);
		logger.info("OrderDto modified: {}", res);
		if (res != null) {
			Optional<OrderBook> optionalTradeOrder = orderRepo.findByOrderId(orderDto.getOrderId());
			if (optionalTradeOrder.isPresent()) {
				OrderBook existingTradeOrder = optionalTradeOrder.get();
				OrderBook tradeOrder = modelMapper.map(orderDto, OrderBook.class);
				tradeOrder.setId(existingTradeOrder.getId());
				tradeOrder.setUserId(userId);
				tradeOrder.setUpdatedDate(new Date());
				tradeOrder.setOrderId(res.getData().getOrderId());
				tradeOrder.setStatus(OrderStatus.ORDER_MODIFIED.toString());
				orderRepo.save(tradeOrder);
				return new OrderResponse(OrderResponse.Status.SUCCESS, tradeOrder.getOrderId(),
						"OrderDto modified successfully");
			} else {
				throw new OrderException("OrderDto not found");
			}
		}
		throw new OrderException("Failed to modify order");
	}

	private ModifyOrderResponse modifyOrderUpstox(String accessToken, PlaceOrder placeOrder) throws ApiException {
		// TODO Auto-generated method stub
		ApiClient defaultClient = Configuration.getDefaultApiClient();

		OAuth OAUTH2 = (OAuth) defaultClient.getAuthentication("OAUTH2");
		OAUTH2.setAccessToken(accessToken);

		OrderApi apiInstance = new OrderApi();
		ModifyOrderRequest body = new ModifyOrderRequest();
		body.setQuantity(placeOrder.getQuantity());
		body.setValidity(ModifyOrderRequest.ValidityEnum.valueOf(placeOrder.getValidity()));
		body.setPrice((float) placeOrder.getPrice());
		body.setDisclosedQuantity(placeOrder.getDisclosedQuantity());
		body.setTriggerPrice((float) placeOrder.getTriggerPrice());
		body.setOrderType(ModifyOrderRequest.OrderTypeEnum.valueOf(placeOrder.getOrderType()));
		body.setOrderId(placeOrder.getOrderId());
		String apiVersion = "2.0"; // String | API Version Header
		ModifyOrderResponse result = null;
		try {
			result = apiInstance.modifyOrder(body, apiVersion);
			result.setStatus(ModifyOrderResponse.StatusEnum.SUCCESS);
		} catch (ApiException e) {
			logger.error("Exception when calling OrderApi#modifyOrder {} {} {}", e.getCode(), e.getMessage(),
					e.getResponseBody());
			throw e;
		}
		return result;
	}

	@Override
	public OrderResponse cancelOrder(String accessToken, String userId, String orderId) throws Exception {
		CancelOrderResponse res = cancelOrderUpstox(accessToken, orderId);
		logger.info("OrderDto cancelled: {}", res);
		if (res != null) {
			List<OrderBook> list = upstoxPortfolioService.orderBook(accessToken, userId);
			OrderBook createdOrder = list.stream().filter(order -> order.getOrderId().equals(res.getData().getOrderId())).collect(Collectors.toList()).get(0);
//			Optional<OrderBook> orderOptional = orderRepo.findByOrderId(orderId);
//			if (orderOptional.isPresent()) {
//				OrderBook tradeOrder = orderOptional.get();
//				if (tradeOrder.getStatus().equals(OrderStatus.ORDER_CANCELLED.toString())) {
//					throw new ApiException("OrderDto already closed by user");
//				}
//				tradeOrder.setUserId(userId);
//				tradeOrder.setOrderId(res.getData().getOrderId());
//				tradeOrder.setStatus(OrderStatus.ORDER_CANCELLED.toString());
//				orderRepo.save(tradeOrder);
//				return new OrderResponse(OrderResponse.Status.SUCCESS, tradeOrder.getOrderId(),
//						"OrderDto cancelled successfully");
//			} else {
//				throw new OrderException("OrderDto not found");
//			}
			return new OrderResponse(
				    createdOrder.getStatus().equals("rejected") ? OrderResponse.Status.FAILURE : OrderResponse.Status.SUCCESS,
				    createdOrder.getOrderId(),
				    createdOrder.getStatus() + (createdOrder.getStatusMessage() != null ? " : " + createdOrder.getStatusMessage() : "")
				);
		}
		throw new OrderException("Failed to cancel order");
	}

	private CancelOrderResponse cancelOrderUpstox(String accessToken, String orderId) throws ApiException {
		ApiClient defaultClient = Configuration.getDefaultApiClient();
		OAuth OAUTH2 = (OAuth) defaultClient.getAuthentication("OAUTH2");
		OAUTH2.setAccessToken(accessToken);
		OrderApi apiInstance = new OrderApi();
		String apiVersion = "2.0"; // String | API Version Header
		CancelOrderResponse result = null;
		try {
			result = apiInstance.cancelOrder(orderId, apiVersion);
			result.setStatus(CancelOrderResponse.StatusEnum.SUCCESS);
			logger.info("OrderDto cancelled: {}", result);
		} catch (ApiException e) {
			logger.error("Exception when calling OrderApi#cancelOrder {} {} {}", e.getCode(), e.getMessage(),
					e.getResponseBody());
			throw e;
		}
		return result;
	}

	@Override
	public OrderBook orderDetails(String accessToken, String userId, String orderId) throws Exception {
		GetOrderResponse res = orderDetailsUpstox(accessToken, orderId);
		logger.info("OrderDto details: {}", res);
		if (res != null) {
			Optional<OrderBook> orderOptional = orderRepo.findByOrderId(orderId);
			if (orderOptional.isPresent()) {
				if(res.getData().size()<=0) {
					throw new OrderException("Check OrderDto Book");
				}
				OrderData data = res.getData().get(0);
				OrderBook tradeOrder = orderOptional.get();
				tradeOrder.setUserId(userId);
//				tradeOrder.setStatus(OrderStatus.ORDER_DETAIL_FULFILL.toString());
				tradeOrder.setAveragePrice(data.getAveragePrice());
				tradeOrder.setStatus(data.getStatus());
				tradeOrder.setFilledQuantity(data.getFilledQuantity());
				tradeOrder.setInstrumentToken(data.getInstrumentToken());
				tradeOrder.setOrderType(data.getOrderType().toString());
				tradeOrder.setPrice(data.getPrice());
				tradeOrder.setProduct(data.getProduct().toString());
				tradeOrder.setQuantity(data.getQuantity());
				tradeOrder.setTransactionType(data.getTransactionType().toString());
				orderRepo.save(tradeOrder);
				return tradeOrder;
			} else {
				throw new OrderException("OrderDto not found");
			}
		}
		throw new OrderException("Failed to get order details");
	}

	private GetOrderResponse orderDetailsUpstox(String accessToken, String orderId) throws ApiException {
		ApiClient defaultClient = Configuration.getDefaultApiClient();
		OAuth OAUTH2 = (OAuth) defaultClient.getAuthentication("OAUTH2");
		OAUTH2.setAccessToken(accessToken);
		OrderApi apiInstance = new OrderApi();
		String apiVersion = "2.0"; // String | API Version Header
		GetOrderResponse result = null;
		try {
			result = apiInstance.getOrderDetails(apiVersion, orderId, "");// pass tag if available
		} catch (ApiException e) {
			logger.error("Exception when calling OrderApi#getOrderDetails {} {} {}", e.getCode(), e.getMessage(),
					e.getResponseBody());
			throw e;
		}
		return result;
	}

	@Override
	public List<Positions> getPositions(String accessToken, String userId) throws Exception {
	    GetPositionResponse res = getPositionsFromUpstox(accessToken);
	    logger.info("Positions: {}", res);
	    if (res != null) {
	        ModelMapper modelMapper = new ModelMapper();
	        modelMapper.addMappings(new PropertyMap<PositionData, Positions>() {
	            @Override
	            protected void configure() {
	                map().setBuyQuantity(source.getDayBuyQuantity());
	                map().setSellQuantity(source.getDaySellQuantity());
	            }
	        });
	        List<Positions> positions = res.getData().stream().map(position -> {
	            Optional<Positions> optionalPosition = positionsRepo.findByTradingSymbolAndUserId(position.getTradingsymbol(),
	                    userId);
	            Positions mappedPosition = modelMapper.map(position, Positions.class);
	            if (optionalPosition.isPresent()) {
	                Positions existingPosition = optionalPosition.get();
	                mappedPosition.setId(existingPosition.getId());
	            }
	            mappedPosition.setUserId(userId);
	            mappedPosition.setBroker("upstox");
	            return mappedPosition;
	        }).collect(Collectors.toList());
	        positionsRepo.saveAll(positions);
	        return positions;
	    }
	    throw new OrderException("Failed to fetch positions");
	}

	public GetPositionResponse getPositionsFromUpstox(String accessToken) throws ApiException {
		ApiClient defaultClient = Configuration.getDefaultApiClient();
		OAuth OAUTH2 = (OAuth) defaultClient.getAuthentication("OAUTH2");
		OAUTH2.setAccessToken(accessToken);
		PortfolioApi apiInstance = new PortfolioApi();
		String apiVersion = "2.0"; // String | API Version Header
		GetPositionResponse result = null;
		try {
			result = apiInstance.getPositions(apiVersion);
		} catch (ApiException e) {
			logger.error("Exception when calling PortfolioApi#getPositions {} {} {}", e.getCode(), e.getMessage(),
					e.getResponseBody());
			throw e;
		}
		return result;
	}

	@Override
	public List<LtpQuotes> getLtpQuotes(String accessToken, String symbol) throws Exception {
		GetMarketQuoteLastTradedPriceResponse res = getLtpQuotesFromUpstox(accessToken, symbol);

		if (res != null) {
			Map<String, MarketQuoteSymbolLtp> responseData = res.getData();

			if (responseData != null) {
				Map<String, MarketQuoteSymbolLtp> instrumentTokenMap = responseData.values().stream()
						.collect(Collectors.toMap(
								MarketQuoteSymbolLtp::getInstrumentToken,
								Function.identity()
						));

				List<String> symbols = Arrays.asList(symbol.split(","));

				return symbols.stream()
						.map(s -> {
							MarketQuoteSymbolLtp quote = instrumentTokenMap.get(s);
							return LtpQuotes.builder()
									.instrumentkey(s)
									.price(quote != null ? quote.getLastPrice() : null)
									.build();
						})
						.toList();
			}
			return null;
		}

		throw new MarketException("Failed to fetch LTP quotes");
	}
	
	@Override
	public LtpQuotes getOneLtpQuotes(String accessToken, String symbol) throws Exception {
	    GetMarketQuoteLastTradedPriceResponse res = getLtpQuotesFromUpstox(accessToken, symbol);
	    if (res != null && res.getData() != null && !res.getData().isEmpty()) {
	        Map.Entry<String, MarketQuoteSymbolLtp> entry = res.getData().entrySet().iterator().next();
	        MarketQuoteSymbolLtp marketQuote = entry.getValue();

	        return LtpQuotes.builder()
	                .instrumentkey(symbol)
	                .price(marketQuote.getLastPrice())
	                .build();
	    }
		logger.info(String.valueOf(res));
//	    throw new MarketException("Failed to fetch LTP quotes or empty response");
        return new LtpQuotes(symbol, null);
    }

	private GetMarketQuoteLastTradedPriceResponse getLtpQuotesFromUpstox(String accessToken, String symbol)
			throws ApiException {
		ApiClient defaultClient = Configuration.getDefaultApiClient();

		OAuth OAUTH2 = (OAuth) defaultClient.getAuthentication("OAUTH2");
		OAUTH2.setAccessToken(accessToken);

		MarketQuoteApi apiInstance = new MarketQuoteApi();
		String apiVersion = "2.0"; // String | API Version Header
		GetMarketQuoteLastTradedPriceResponse result = null;
		try {
			result = apiInstance.ltp(symbol, apiVersion);
		} catch (ApiException e) {
			logger.error("Exception when calling MarketQuoteApi#ltp {} {} {}", e.getCode(), e.getMessage(),
					e.getResponseBody());
			throw e;
		}
		return result;
	}

	@Override
	public List<HistoricalQuotes> getHistoricalQuotes(String symbol, String interval, String toDate,
													  String fromDate) throws Exception {
		var res = getHistoricalQuotesFromUpstox(symbol, interval, toDate, fromDate);
		if (res != null && res.getData() != null) {
			var data = res.getData().getCandles();
			if(data==null || data.isEmpty()){
				throw new MarketException("No Historical Quotes Found from Upstox");
			}
			List<HistoricalQuotes> quotes= data.stream()
						.map(candle -> HistoricalQuotes.builder()
								.startTime(candle.get(0).toString())
								.openPrice(convertToDouble(candle.get(1)))
								.highPrice(convertToDouble(candle.get(2)))
								.lowPrice(convertToDouble(candle.get(3)))
								.closePrice(convertToDouble(candle.get(4)))
								.volume(convertToDouble(candle.get(5)))
								.openInterest(convertToDouble(candle.get(6)))
								.build())
						.toList();
			quotes.forEach(quote-> logger.info("{} : {}", symbol, quote));
			return quotes;
		}
		throw new MarketException("Failed to fetch historical quotes");
	}

	private double convertToDouble(Object obj) {
		return obj instanceof Double d ? d : Double.parseDouble(obj.toString());
	}

	private long convertToLong(Object obj) {
		return obj instanceof Long l ? l : Long.parseLong(obj.toString());
	}

	private GetHistoricalCandleResponse getHistoricalQuotesFromUpstox(String instrumentKey, String interval,
			String toDate, String fromDate) throws ApiException {
		HistoryApi apiInstance = new HistoryApi();
		String apiVersion = "2.0";
		GetHistoricalCandleResponse result = null;
		try {
			result = apiInstance.getHistoricalCandleData1(instrumentKey, interval, toDate, fromDate, apiVersion);
		} catch (ApiException e) {
			logger.error("Exception when calling HistoryApi#getHistoricalCandleData {} {} {}", e.getCode(),
					e.getMessage(), e.getResponseBody());
			throw e;
		}
		return result;
	}
}
