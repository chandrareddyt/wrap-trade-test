package com.trade.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trade.entity.Positions;

public interface PositionsRepo extends JpaRepository<Positions, Long>{

	Optional<Positions> findByTradingSymbolAndUserId(String tradingsymbol, String userId);

}
