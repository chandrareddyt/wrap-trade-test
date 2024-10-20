package com.trade.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserAuthentication {
	
	@NotNull(message = "Broker is required")
	private String broker;
	@NotNull(message = "Client User Id is required")
	private String clientUserId;
	@NotNull(message = "Api Key is required")
	private String clientApiKey;
	@NotNull(message = "Api Secret is required")
	private String clientApiSecret;
	@NotNull(message = "Redirect Url is required")
	private String redirectUrl;
	@NotNull(message = "Authentication code is required")
	private String authenticationCode;
	@NotNull(message = "Access Token is required")
	@Column(length = 500)
	private String accessToken;
	@NotNull(message = "Public Token is required")
	@Column(length = 500)
	private String publicToken;
}
