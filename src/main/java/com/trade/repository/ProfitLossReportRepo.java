package com.trade.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.trade.entity.ProfitLossReport;

public interface ProfitLossReportRepo extends JpaRepository<ProfitLossReport, Long>{
	
	@Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM ProfitLossReport p WHERE p.userId = :userId AND p.broker = :broker AND p.quantity = :quantity AND p.isin = :isin AND p.scripName = :scripName AND p.tradeType = :tradeType AND p.buyDate = :buyDate AND p.buyAverage = :buyAverage AND p.sellDate = :sellDate AND p.sellAverage = :sellAverage AND p.buyAmount = :buyAmount AND p.sellAmount = :sellAmount")
	boolean existsByAllFields(
	    @Param("userId") String userId,
	    @Param("broker") String broker,
	    @Param("quantity") double quantity,
	    @Param("isin") String isin,
	    @Param("scripName") String scripName,
	    @Param("tradeType") String tradeType,
	    @Param("buyDate") String buyDate,
	    @Param("buyAverage") double buyAverage,
	    @Param("sellDate") String sellDate,
	    @Param("sellAverage") double sellAverage,
	    @Param("buyAmount") double buyAmount,
	    @Param("sellAmount") double sellAmount
	);

	List<ProfitLossReport> findAllByUserIdAndBroker(String userId, String string);

}
