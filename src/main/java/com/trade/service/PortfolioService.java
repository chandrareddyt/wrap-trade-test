package com.trade.service;

import java.util.List;

import com.trade.entity.FundMargin;
import com.trade.entity.Holdings;
import com.trade.entity.OrderBook;
import com.trade.entity.ProfitLossReport;
import com.trade.entity.UserDetailsBroker;

public interface PortfolioService {

	public List<OrderBook> orderBook(String accessToken, String userId) throws Exception;

	public List<Holdings> holdings(String accessToken, String userId) throws Exception;

	public List<ProfitLossReport> profitLossReport(String accessToken, String userId, String fromDate,
			String toDate, String segment, String financialYear, Integer pageNumber, Integer pageSize)
			throws Exception;

	public UserDetailsBroker userProfile(String accessToken, String userId) throws Exception;

	public List<FundMargin> fundAndMargin(String accessToken, String userId) throws Exception;

}
