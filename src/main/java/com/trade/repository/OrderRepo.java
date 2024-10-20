package com.trade.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trade.entity.TradeOrder;

public interface OrderRepo extends JpaRepository<TradeOrder, Long>{

	Optional<TradeOrder> findByOrderId(String orderId);

}
