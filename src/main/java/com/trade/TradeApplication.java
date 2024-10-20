package com.trade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@SpringBootApplication
@OpenAPIDefinition(
	    info = @Info(title = "Wealth Pandit Trade API", version = "v1"),
	    servers = {@Server(url = "https://uat.tradeapi.wealthpandit.com/"), @Server(url = "http://localhost:8080/"), @Server(url = "http://localhost:8081/")}
)
public class TradeApplication {

	public static void main(String[] args) {
		SpringApplication.run(TradeApplication.class);
	}

}
