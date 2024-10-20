package com.trade.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.trade.dto.UserAuthentication;
import com.trade.response.TokenResponse;
import com.trade.service.UserService;
import com.trade.upstox.service.UpstoxUserService;
import com.trade.zerodha.service.ZerodhaUserService;

@RestController
@RequestMapping("/broker/auth")
public class BrokerController {

	private static final Logger logger = LoggerFactory.getLogger(BrokerController.class);

	@Autowired
	private UpstoxUserService upstoxUserService;

	@Autowired
	private ZerodhaUserService zerodhaUserService;

	public BrokerController(@Qualifier("UpstoxUser") UpstoxUserService upstoxUserService,
			@Qualifier("ZerodhaUser") ZerodhaUserService zerodhaUserService) {
		this.upstoxUserService = upstoxUserService;
		this.zerodhaUserService = zerodhaUserService;
	}

	private UserService getUserService(String broker) {
		switch (broker) {
		case "upstox":
			return upstoxUserService;
		case "zerodha":
			return zerodhaUserService;
		default:
			throw new IllegalArgumentException("Invalid broker: " + broker);
		}
	}
	
	@GetMapping("/health")
	public ResponseEntity<String> checkHealth() {
		return ResponseEntity.status(HttpStatus.OK).body("Service is up and running!");
	}

	@GetMapping("/is-authenticated")
	public ResponseEntity<Boolean> isAuthenticated(@RequestParam String broker, @RequestParam String accessToken)
	        throws Exception {
	    logger.info("Checking if user is authenticated for broker: {}", broker);
	    Boolean isAuthenticated = getUserService(broker).isAuthenticated(broker, accessToken);
	    return ResponseEntity.status(HttpStatus.OK).body(isAuthenticated);
	}
	
	@PostMapping("/authenticate-login")
	public ResponseEntity<TokenResponse> authenticateUser(
			@RequestBody UserAuthentication userAuthentication) throws Exception {
		logger.info("Authenticating user for broker: {}", userAuthentication.getBroker());
		TokenResponse tokenResponse= getUserService(userAuthentication.getBroker()).authenticateUser(userAuthentication);
		return ResponseEntity.status(HttpStatus.CREATED).body(tokenResponse);
	}
	
	@GetMapping("/getbrokers")
	public ResponseEntity<List<String>> getBrokers()
	        throws Exception {
	    logger.info("Getting the brokers");
	    List<String> brokersList= upstoxUserService.getBrokers();
	    return ResponseEntity.status(HttpStatus.OK).body(brokersList);
	}
}
