package com.trade.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

	@GetMapping("/health")
	public ResponseEntity<String> checkHealth() {
		return ResponseEntity.status(HttpStatus.OK).body("Service is up and running!");
	}

}
