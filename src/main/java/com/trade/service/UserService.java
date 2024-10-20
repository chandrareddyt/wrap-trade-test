package com.trade.service;

import com.trade.dto.UserAuthentication;
import com.trade.response.TokenResponse;
import com.upstox.ApiException;

public interface UserService {

	public Boolean isAuthenticated(String broker, String accessToken) throws ApiException;

	public TokenResponse authenticateUser(UserAuthentication userAuthentication) throws Exception;

}
