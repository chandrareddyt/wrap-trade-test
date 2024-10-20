package com.trade.zerodha.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.trade.entity.FundMargin;
import com.trade.entity.Holdings;
import com.trade.entity.OrderBook;
import com.trade.entity.ProfitLossReport;
import com.trade.entity.UserDetailsBroker;
import com.trade.service.PortfolioService;
import com.upstox.ApiException;

@Service("ZerodhaPortfolio")
public class ZerodhaPortfolioService implements PortfolioService{

	@Override
	public List<OrderBook> orderBook(String accessToken, String userId) throws ApiException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Holdings> holdings(String accessToken, String userId) throws ApiException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ProfitLossReport> profitLossReport(String accessToken, String userId, String fromDate,
			String toDate, String segment, String financialYear, Integer pageNumber, Integer pageSize)
			throws ApiException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserDetailsBroker userProfile(String accessToken, String userId) throws ApiException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<FundMargin> fundAndMargin(String accessToken, String userId) throws ApiException {
		// TODO Auto-generated method stub
		return null;
	}

}
