package com.trade.controller;

import java.util.List;

import com.trade.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.trade.entity.OrderBook;
import com.trade.entity.Positions;
import com.trade.response.OrderResponse;
import com.trade.service.OrderService;
import com.trade.upstox.service.UpstoxOrderService;
import com.trade.zerodha.service.ZerodhaOrderService;
import com.upstox.api.GetHistoricalCandleResponse;
import com.upstox.api.GetMarketQuoteLastTradedPriceResponse;

@RestController
@RequestMapping("/trade")
public class TradeController {

	private static final Logger logger = LoggerFactory.getLogger(TradeController.class);

	@Autowired
	private UpstoxOrderService upstoxOrderService;

	@Autowired
	private ZerodhaOrderService zerodhaOrderService;

	public TradeController(@Qualifier("UpstoxOrder") UpstoxOrderService upstoxOrderService,
			@Qualifier("ZerodhaOrder") ZerodhaOrderService zerodhaOrderService) {
		this.upstoxOrderService = upstoxOrderService;
		this.zerodhaOrderService = zerodhaOrderService;
	}

	private OrderService getOrderService(String broker) {
		switch (broker) {
		case "upstox":
			return upstoxOrderService;
		case "zerodha":
			return zerodhaOrderService;
		default:
			throw new IllegalArgumentException("Invalid broker: " + broker);
		}
	}

	@GetMapping("/health")
	public ResponseEntity<String> checkHealth() {
		return ResponseEntity.status(HttpStatus.OK).body("Service is up and running!");
	}

	@PostMapping("/order-place")
	public ResponseEntity<OrderResponse> placeOrder(@Validated @RequestBody OrderDto orderDto) throws Exception {
		logger.info("Placing order for: {}", orderDto.getInstrumentToken());
		OrderResponse response = getOrderService(orderDto.getBroker()).placeOrder(orderDto.getAccessToken(),
				getUserName(), orderDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PutMapping("/order-modify")
	public ResponseEntity<OrderResponse> modifyOrder(@Validated @RequestBody OrderDto orderDto) throws Exception {
		logger.info("Modifying order for: {}", orderDto.getInstrumentToken());
		OrderResponse response = getOrderService(orderDto.getBroker()).modifyOrder(orderDto.getAccessToken(),
				getUserName(), orderDto);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@DeleteMapping("/order-cancel")
	public ResponseEntity<OrderResponse> cancelOrder(@Validated @RequestBody OrderDto orderDto) throws Exception {
		String orderId = orderDto.getOrderId();
		logger.info("Cancelling order for: {}", orderId);
		OrderResponse response = getOrderService(orderDto.getBroker()).cancelOrder(orderDto.getAccessToken(),
				getUserName(), orderId);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PostMapping("/orderdetails")
	public ResponseEntity<OrderBook> orderDetails(@Validated @RequestBody OrderDto orderDto) throws Exception {
		// if there is any exception try to pass tag as well, currently its set as null
		logger.info("Fetching order detalis for user: {}", getUserName());
		OrderBook response = getOrderService(orderDto.getBroker()).orderDetails(orderDto.getAccessToken(),
				getUserName(), orderDto.getOrderId());
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PostMapping("/positions")
	public ResponseEntity<List<Positions>> positions(@Validated @RequestBody BaseDto baseDto) throws Exception {
		logger.info("Fetching positions for user: {}", getUserName());
		List<Positions> response = getOrderService(baseDto.getBroker()).getPositions(baseDto.getAccessToken(),
				getUserName());
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PostMapping("/ltp-quotes-list") //enable when list of quotes are required
	public ResponseEntity<List<LtpQuotes>> ltpQuotes(@RequestBody BaseDto baseDto,
			@RequestParam(name = "symbol") String symbol) throws Exception {
		logger.info("Fetching LTP quotes for symbols: {}", symbol);
		List<LtpQuotes> response = getOrderService(baseDto.getBroker())
				.getLtpQuotes(baseDto.getAccessToken(), symbol);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@PostMapping("/ltp-quotes")
	public ResponseEntity<LtpQuotes> getOneltpQuotes(@RequestBody BaseDto baseDto,
			@RequestParam(name = "symbol") String symbol) throws Exception {
		logger.info("Fetching LTP quotes for symbol: {}", symbol);
		LtpQuotes response = getOrderService(baseDto.getBroker())
				.getOneLtpQuotes(baseDto.getAccessToken(), symbol);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PostMapping("/historical-quotes")
	public ResponseEntity<List<HistoricalQuotes>> historicalQuotes(@RequestBody HistoricalQuotesDto dto)
			throws Exception {
		logger.info("Fetching historical quotes for symbol: {}, interval: {}, toDate: {}, fromDate: {}",
				dto.getSymbol(), dto.getInterval(), dto.getToDate(), dto.getFromDate());
		List<HistoricalQuotes> response = getOrderService(dto.getBroker()).getHistoricalQuotes(dto.getSymbol(),
				dto.getInterval(), dto.getToDate(), dto.getFromDate());
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	private String getUserName() {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return user.getUsername();
	}
}
