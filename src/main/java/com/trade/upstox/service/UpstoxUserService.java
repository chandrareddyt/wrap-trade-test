package com.trade.upstox.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.trade.dto.UserAuthentication;
import com.trade.entity.Broker;
import com.trade.exception.AuthException;
import com.trade.repository.BrokerRepo;
import com.trade.service.UserService;
import com.upstox.ApiClient;
import com.upstox.ApiException;
import com.upstox.Configuration;
import com.upstox.api.GetProfileResponse;
import com.upstox.api.TokenResponse;
import com.upstox.auth.OAuth;

import io.swagger.client.api.LoginApi;
import io.swagger.client.api.UserApi;

@Service("UpstoxUser")
public class UpstoxUserService implements UserService {

	private static final Logger logger = LoggerFactory.getLogger(UpstoxUserService.class);

	@Autowired
	private BrokerRepo brokerRepo;
	
	@Override
	public Boolean isAuthenticated(String broker, String accessToken) throws ApiException {
		return isAuthenticated(accessToken);
	}

	@Override
	public com.trade.response.TokenResponse authenticateUser(UserAuthentication userAuthentication) throws Exception {
		try {
			if (userAuthentication.getAuthenticationCode() != null) {
				if (!isAuthenticated(userAuthentication.getAccessToken())) {
					com.trade.response.TokenResponse res = new com.trade.response.TokenResponse();
					res.setAccessToken(getAccessToken(userAuthentication).getAccessToken());
					return res;
				} else {
					throw new AuthException("User is already authenticated");
				}
			} else {
				throw new AuthException("Unable to Authenticate User. Please check the authentication details.");
			}
		} catch (ApiException e) {
			throw e;
		} catch (Exception e) {
			throw new AuthException(e.getMessage());
		}
	}

	private TokenResponse getAccessToken(UserAuthentication userAuthentication) throws ApiException {
		LoginApi apiInstance = new LoginApi();
		String apiVersion = "2.0";
		String code = userAuthentication.getAuthenticationCode();
		String clientId = userAuthentication.getClientApiKey();
		String clientSecret = userAuthentication.getClientApiSecret();
		String redirectUri = userAuthentication.getRedirectUrl();
		String grantType = "authorization_code";
		TokenResponse result = null;
		try {
			result = apiInstance.token(apiVersion, code, clientId, clientSecret, redirectUri, grantType);
			logger.info("Access Token: {}", result.getAccessToken());
		} catch (ApiException e) {
			logger.error("Exception when calling LoginApi#token {} {} {}", e.getCode(), e.getMessage(),
					e.getResponseBody());
			throw e;
		}
		return result;
	}

	private boolean isAuthenticated(String accessToken) {
		if (accessToken == null) {
			return false;
		}
		ApiClient defaultClient = Configuration.getDefaultApiClient();

		OAuth OAUTH2 = (OAuth) defaultClient.getAuthentication("OAUTH2");
		OAUTH2.setAccessToken(accessToken);

		UserApi apiInstance = new UserApi();
		String apiVersion = "2.0";
		try {
			GetProfileResponse result = apiInstance.getProfile(apiVersion);
			return Optional.ofNullable(result).map(GetProfileResponse::getStatus)
					.map(status -> status.equals(GetProfileResponse.StatusEnum.SUCCESS)).orElse(false);
		} catch (ApiException e) {
			logger.error("Exception when calling UserApi#getProfile {} {} {}", e.getCode(), e.getMessage(),
					e.getResponseBody());
			return false;
		}
	}

	public List<String> getBrokers() {
		List<Broker> activeBrokers = brokerRepo.findByIsActive(true);
		
		return activeBrokers.stream()
                .map(Broker::getBroker)
                .collect(Collectors.toList());
	}

}
