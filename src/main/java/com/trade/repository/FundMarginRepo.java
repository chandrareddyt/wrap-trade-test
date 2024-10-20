package com.trade.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trade.entity.FundMargin;

public interface FundMarginRepo extends JpaRepository<FundMargin, Long>{

	FundMargin findByUserIdAndMarket(String userId, String market);

}
