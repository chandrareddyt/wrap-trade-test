package com.trade.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trade.dto.BaseDto;
import com.trade.dto.PlReportDto;
import com.trade.entity.FundMargin;
import com.trade.entity.Holdings;
import com.trade.entity.OrderBook;
import com.trade.entity.ProfitLossReport;
import com.trade.entity.UserDetailsBroker;
import com.trade.service.PortfolioService;
import com.trade.upstox.service.UpstoxPortfolioService;
import com.trade.zerodha.service.ZerodhaPortfolioService;

@RestController
@RequestMapping("/portfolio")
public class PortfolioController {

	private static final Logger logger = LoggerFactory.getLogger(PortfolioController.class);

	@Autowired
	private UpstoxPortfolioService upstoxPortfolioService;

	@Autowired
	private ZerodhaPortfolioService zerodhaPortfolioService;

	public PortfolioController(@Qualifier("UpstoxPortfolio") UpstoxPortfolioService upstoxPortfolioService,
			@Qualifier("ZerodhaPortfolio") ZerodhaPortfolioService zerodhaPortfolioService) {
		this.upstoxPortfolioService = upstoxPortfolioService;
		this.zerodhaPortfolioService = zerodhaPortfolioService;
	}

	private PortfolioService getPortfolioService(String broker) {
		switch (broker) {
		case "upstox":
			return upstoxPortfolioService;
		case "zerodha":
			return zerodhaPortfolioService;
		default:
			throw new IllegalArgumentException("Invalid broker: " + broker);
		}
	}

	@GetMapping("/health")
	public ResponseEntity<String> checkHealth() {
		return ResponseEntity.status(HttpStatus.OK).body("Service is up and running!");
	}

	@GetMapping("/orderbook")
	public ResponseEntity<List<OrderBook>> orderBook(@Validated @RequestBody BaseDto baseDto) throws Exception {
		logger.info("Fetching upstoxOrderService book for user: {}", getUserName());
		List<OrderBook> response = getPortfolioService(baseDto.getBroker()).orderBook(baseDto.getAccessToken(),
				getUserName());
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@GetMapping("/holdings")
	public ResponseEntity<List<Holdings>> holdings(@Validated @RequestBody BaseDto baseDto) throws Exception {
		logger.info("Fetching holdings for user: {}", getUserName());
		List<Holdings> response = getPortfolioService(baseDto.getBroker()).holdings(baseDto.getAccessToken(),
				getUserName());
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@GetMapping("/profit-loss-report")
	public ResponseEntity<List<ProfitLossReport>> profitLossReport(@Validated @RequestBody PlReportDto plReportDto)
			throws Exception {
		logger.info("Fetching profit loss report for user: {}", getUserName());
		List<ProfitLossReport> response = getPortfolioService(plReportDto.getBroker()).profitLossReport(
				plReportDto.getAccessToken(), getUserName(), plReportDto.getFromDate(), plReportDto.getToDate(),
				plReportDto.getSegment(), plReportDto.getFinancialYear(), plReportDto.getPageNumber(),
				plReportDto.getPageSize());
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@GetMapping("/profile")
	public ResponseEntity<UserDetailsBroker> userProfile(@Validated @RequestBody BaseDto baseDto) throws Exception {
		logger.info("Fetching user profile for: {}", getUserName());
		UserDetailsBroker response = getPortfolioService(baseDto.getBroker()).userProfile(baseDto.getAccessToken(),
				getUserName());
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@GetMapping("/fund-margin")
	public ResponseEntity<List<FundMargin>> fundAndMargin(@Validated @RequestBody BaseDto baseDto) throws Exception {
		logger.info("Fetching fund and margin for: {}", getUserName());
		List<FundMargin> response = getPortfolioService(baseDto.getBroker()).fundAndMargin(baseDto.getAccessToken(),
				getUserName());
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	private String getUserName() {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return user.getUsername();
	}

}