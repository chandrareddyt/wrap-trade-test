package com.trade.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trade.entity.UserDetailsBroker;

public interface UserDetailsBrokerRepo extends JpaRepository<UserDetailsBroker, Long>{

	Optional<UserDetailsBroker> findByWpUserIdAndBroker(String userId, String string);

}
