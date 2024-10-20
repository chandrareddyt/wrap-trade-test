package com.trade.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BaseDto {

	@NotNull(message = "Broker Name is required")
	private String broker;
	@NotNull(message = "Access Token is required")
	private String accessToken;
}
