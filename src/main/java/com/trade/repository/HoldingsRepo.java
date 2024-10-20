package com.trade.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trade.entity.Holdings;

public interface HoldingsRepo extends JpaRepository<Holdings, Long>{

	Optional<Holdings> findByIsinAndUserId(String isin, String userId);

}
