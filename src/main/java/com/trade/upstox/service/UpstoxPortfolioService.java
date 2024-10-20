package com.trade.upstox.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.trade.entity.FundMargin;
import com.trade.entity.Holdings;
import com.trade.entity.OrderBook;
import com.trade.entity.ProfitLossReport;
import com.trade.entity.UserDetailsBroker;
import com.trade.exception.PortfolioException;
import com.trade.repository.FundMarginRepo;
import com.trade.repository.HoldingsRepo;
import com.trade.repository.OrderBookRepo;
import com.trade.repository.ProfitLossReportRepo;
import com.trade.repository.UserDetailsBrokerRepo;
import com.trade.service.PortfolioService;
import com.upstox.ApiClient;
import com.upstox.ApiException;
import com.upstox.Configuration;
import com.upstox.api.GetHoldingsResponse;
import com.upstox.api.GetOrderBookResponse;
import com.upstox.api.GetProfileResponse;
import com.upstox.api.GetTradeWiseProfitAndLossDataResponse;
import com.upstox.api.GetUserFundMarginResponse;
import com.upstox.api.ProfileData;
import com.upstox.api.UserFundMarginData;
import com.upstox.auth.OAuth;

import io.swagger.client.api.OrderApi;
import io.swagger.client.api.PortfolioApi;
import io.swagger.client.api.TradeProfitAndLossApi;
import io.swagger.client.api.UserApi;

@Service("UpstoxPortfolio")
public class UpstoxPortfolioService implements PortfolioService {

	private static final Logger logger = LoggerFactory.getLogger(UpstoxPortfolioService.class);

	@Autowired
	@Qualifier("UpstoxPortfolioModelMapper")
	private ModelMapper modelMapper;

	@Autowired
	private OrderBookRepo orderBookRepo;

	@Autowired
	private HoldingsRepo holdingsRepo;

	@Autowired
	private ProfitLossReportRepo profitLossReportRepo;

	@Autowired
	private UserDetailsBrokerRepo userDetailsBrokerRepo;

	@Autowired
	private FundMarginRepo fundMarginRepo;

	public enum OrderStatus {
		ORDER_PLACED, ORDER_MODIFIED, ORDER_CANCELLED, ORDER_CLOSED_BY_USER, ORDER_CLOSED
	}

	@Override
	public List<OrderBook> orderBook(String accessToken, String userId) throws Exception {
	    GetOrderBookResponse res = getOrderBookFromUpstox(accessToken);
	    logger.info("OrderDto book response: {}", res);
	    if (res != null) {
	        List<OrderBook> orderBook = res.getData().stream().map(order -> {
	        	Optional<OrderBook> existingOrderBook = orderBookRepo.findByOrderId(order.getOrderId());
				OrderBook orderBookEntity = modelMapper.map(order, OrderBook.class);
				existingOrderBook.ifPresent(existing -> {
					orderBookEntity.setId(existing.getId());
				});
				orderBookEntity.setExchange(order.getExchange().toString());
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				Optional<String> opt = Optional.ofNullable(order.getOrderTimestamp());
				Date date = opt.map(d -> {
					try {
						return formatter.parse(d);
					} catch (ParseException e) {
						throw new RuntimeException(e);
					}
				}).orElse(null);
				orderBookEntity.setCreatedDate(date);
				orderBookEntity.setUserId(userId);
				orderBookEntity.setBroker("upstox");
	            return orderBookEntity;
	        }).collect(Collectors.toList());

	        orderBookRepo.saveAll(orderBook);
	        return orderBookRepo.findAllByUserIdAndBroker(userId, "upstox");
	    }
	    throw new PortfolioException("Failed to fetch order book");
	}

	private GetOrderBookResponse getOrderBookFromUpstox(String accessToken) throws ApiException {
		ApiClient defaultClient = Configuration.getDefaultApiClient();
		OAuth OAUTH2 = (OAuth) defaultClient.getAuthentication("OAUTH2");
		OAUTH2.setAccessToken(accessToken);
		OrderApi apiInstance = new OrderApi();
		String apiVersion = "2.0"; // String | API Version Header
		GetOrderBookResponse result = null;
		try {
			result = apiInstance.getOrderBook(apiVersion);
		} catch (ApiException e) {
			logger.error("Exception when calling PortfolioApi#getOrderBook {} {} {}", e.getCode(), e.getMessage(),
					e.getResponseBody());
			throw e;
		}
		return result;
	}

	@Override
	public List<Holdings> holdings(String accessToken, String userId) throws Exception {
		GetHoldingsResponse res = getHoldingsFromUpstox(accessToken);
		logger.info("Holdings response: {}", res);
		if (res != null) {
			List<Holdings> holdings = res.getData().stream().map(holding -> {
				Optional<Holdings> existingHolding = holdingsRepo.findByIsinAndUserId(holding.getIsin(), userId);
				Holdings holdingEntity = modelMapper.map(holding, Holdings.class);
				if (existingHolding.isPresent()) {
					holdingEntity.setId(existingHolding.get().getId());
				}
				holdingEntity.setUserId(userId);
				holdingEntity.setBroker("upstox");
				return holdingEntity;
			}).collect(Collectors.toList());
			holdingsRepo.saveAll(holdings);
			return holdings;
		}
		throw new PortfolioException("Failed to fetch holdings");
	}

	private GetHoldingsResponse getHoldingsFromUpstox(String accessToken) throws ApiException {
		ApiClient defaultClient = Configuration.getDefaultApiClient();
		OAuth OAUTH2 = (OAuth) defaultClient.getAuthentication("OAUTH2");
		OAUTH2.setAccessToken(accessToken);
		PortfolioApi apiInstance = new PortfolioApi();
		String apiVersion = "2.0"; // String | API Version Header
		GetHoldingsResponse result = null;
		try {
			result = apiInstance.getHoldings(apiVersion);
			System.out.println(result);
		} catch (ApiException e) {
			logger.error("Exception when calling PortfolioApi#getHoldings {} {} {}", e.getCode(), e.getMessage(),
					e.getResponseBody());
			throw e;
		}
		return result;
	}

	@Override
	public List<ProfitLossReport> profitLossReport(String accessToken, String userId, String fromDate, String toDate,
			String segment, String financialYear, Integer pageNumber, Integer pageSize) throws Exception {
		GetTradeWiseProfitAndLossDataResponse res = getProfitLossReportFromUpstox(accessToken, segment, financialYear,
				fromDate, toDate, pageNumber, pageSize);
		logger.info("Profit loss report response: {}", res);
		if (res != null) {
			List<ProfitLossReport> profitLossReports = res.getData().stream().map(report -> {
			    ProfitLossReport reportEntity = modelMapper.map(report, ProfitLossReport.class);
			    reportEntity.setUserId(userId);
			    reportEntity.setBroker("upstox");
			    return reportEntity;
			}).collect(Collectors.toList());

			List<ProfitLossReport> uniqueProfitLossReports = profitLossReports.stream()
				    .filter(report -> !profitLossReportRepo.existsByAllFields(
				        report.getUserId(),
				        report.getBroker(),
				        report.getQuantity(),
				        report.getIsin(),
				        report.getScripName(),
				        report.getTradeType(),
				        report.getBuyDate(),
				        report.getBuyAverage(),
				        report.getSellDate(),
				        report.getSellAverage(),
				        report.getBuyAmount(),
				        report.getSellAmount()
				    ))
				    .collect(Collectors.toList());

			profitLossReportRepo.saveAll(uniqueProfitLossReports);

			return profitLossReportRepo.findAllByUserIdAndBroker(userId, "upstox");
		}
		throw new PortfolioException("Failed to fetch profit loss report");
	}

	private GetTradeWiseProfitAndLossDataResponse getProfitLossReportFromUpstox(String accessToken, String segment,
			String financialYear, String fromDate, String toDate, Integer pageNumber, Integer pageSize)
			throws ApiException {
		ApiClient defaultClient = Configuration.getDefaultApiClient();
		OAuth OAUTH2 = (OAuth) defaultClient.getAuthentication("OAUTH2");
		OAUTH2.setAccessToken(accessToken);
		TradeProfitAndLossApi apiInstance = new TradeProfitAndLossApi();
		String apiVersion = "2.0"; // String | API Version Header
		GetTradeWiseProfitAndLossDataResponse result = null;
		try {
			result = apiInstance.getTradeWiseProfitAndLossData(segment, financialYear, pageNumber, pageSize, apiVersion,
					fromDate, toDate);
		} catch (ApiException e) {
			logger.error("Exception when calling PortfolioApi#getProfitLossReport {} {} {}", e.getCode(),
					e.getMessage(), e.getResponseBody());
			throw e;
		}
		return result;
	}

	@Override
	public UserDetailsBroker userProfile(String accessToken, String userId) throws Exception {
		GetProfileResponse res = getUserProfileFromUpstox(accessToken);
		logger.info("User profile response: {}", res);
		if (res != null) {
			modelMapper.typeMap(ProfileData.class, UserDetailsBroker.class).addMappings(mapper -> mapper.skip(UserDetailsBroker::setId));
			UserDetailsBroker user = modelMapper.map(res.getData(), UserDetailsBroker.class);
			Optional<UserDetailsBroker> existingUser = userDetailsBrokerRepo.findByWpUserIdAndBroker(userId, "upstox");
			if (existingUser.isPresent()) {
				user.setId(existingUser.get().getId());
			}
			user.setWpUserId(userId);
			user.setBroker("upstox");
			user.setBrokerUserId(res.getData().getUserId());
			userDetailsBrokerRepo.save(user);
			return user;
		}
		throw new PortfolioException("Failed to fetch user profile");
	}

	private GetProfileResponse getUserProfileFromUpstox(String accessToken) throws ApiException {
		ApiClient defaultClient = Configuration.getDefaultApiClient();

		OAuth OAUTH2 = (OAuth) defaultClient.getAuthentication("OAUTH2");
		OAUTH2.setAccessToken(accessToken);

		UserApi apiInstance = new UserApi();
		String apiVersion = "2.0";
		GetProfileResponse result = null;
		try {
			result = apiInstance.getProfile(apiVersion);
			System.out.println(result);
		} catch (ApiException e) {
			logger.error("Exception when calling PortfolioApi#getUserProfile {} {} {}", e.getCode(), e.getMessage(),
					e.getResponseBody());
			throw e;
		}
		return result;
	}

	@Override
	public List<FundMargin> fundAndMargin(String accessToken, String userId) throws Exception {
		GetUserFundMarginResponse res = getFundAndMarginFromUpstox(accessToken);
		logger.info("Fund and margin response: {}", res);
		if (res != null && res.getData() != null) {
			UserFundMarginData commodityData = res.getData().get("commodity");
			UserFundMarginData equityData = res.getData().get("equity");

			List<FundMargin> fundMargin = new ArrayList<>();
			if (commodityData != null) {
				Optional<FundMargin> commodityMarginOptional = Optional.ofNullable(fundMarginRepo.findByUserIdAndMarket(userId, "commodity"));

				FundMargin commodityMargin = commodityMarginOptional.orElseGet(() -> {
					FundMargin newMargin = new FundMargin();
					newMargin.setUserId(userId);
					newMargin.setMarket("commodity");
					return newMargin;
				});

				commodityMargin.setUsedMargin(commodityData.getUsedMargin());
				commodityMargin.setAvailableMargin(commodityData.getAvailableMargin());

				fundMarginRepo.save(commodityMargin);
				fundMargin.add(commodityMargin);
			}

			if (equityData != null) {
				Optional<FundMargin> equityMarginOptional = Optional.ofNullable(fundMarginRepo.findByUserIdAndMarket(userId, "equity"));

				FundMargin equityMargin = equityMarginOptional.orElseGet(() -> {
					FundMargin newMargin = new FundMargin();
					newMargin.setUserId(userId);
					newMargin.setMarket("equity");
					return newMargin;
				});

				equityMargin.setUsedMargin(equityData.getUsedMargin());
				equityMargin.setAvailableMargin(equityData.getAvailableMargin());

				fundMarginRepo.save(equityMargin);
				fundMargin.add(equityMargin);
			}

			return fundMargin;
		}
		throw new PortfolioException("Failed to fetch fund and margin");
	}

	private GetUserFundMarginResponse getFundAndMarginFromUpstox(String accessToken) throws ApiException {
		ApiClient defaultClient = Configuration.getDefaultApiClient();

		OAuth OAUTH2 = (OAuth) defaultClient.getAuthentication("OAUTH2");
		OAUTH2.setAccessToken(accessToken);

		UserApi apiInstance = new UserApi();
		String apiVersion = "2.0"; // String | API Version Header
		String segment = "";
		GetUserFundMarginResponse result = null;
		try {
			result = apiInstance.getUserFundMargin(apiVersion, segment);
			System.out.println(result);
		} catch (ApiException e) {
			logger.error("Exception when calling PortfolioApi#getFundAndMargin {} {} {}", e.getCode(), e.getMessage(),
					e.getResponseBody());
			throw e;
		}
		return result;
	}

}
