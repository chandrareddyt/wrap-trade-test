package com.trade.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trade.entity.OrderBook;

public interface OrderBookRepo extends JpaRepository<OrderBook, Long>{

	Optional<OrderBook> findByOrderId(String orderId);

	boolean existsByOrderId(String orderId);

	List<OrderBook> findAllByUserIdAndBroker(String userId, String string);

}
