package com.trade.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trade.entity.Broker;

public interface BrokerRepo extends JpaRepository<Broker, Long>{

	List<Broker> findByIsActive(boolean isActive);


}
