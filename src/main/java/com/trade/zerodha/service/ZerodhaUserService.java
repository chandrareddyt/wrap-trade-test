package com.trade.zerodha.service;

import com.trade.exception.AuthException;
import com.trade.exception.CustomApiException;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.SessionExpiryHook;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.Profile;
import com.zerodhatech.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.trade.dto.UserAuthentication;
import com.trade.response.TokenResponse;
import com.trade.service.UserService;
import com.upstox.ApiException;

import java.io.IOException;

@Service("ZerodhaUser")
public class ZerodhaUserService implements UserService{

	private static final Logger logger = LoggerFactory.getLogger(ZerodhaUserService.class);

	@Override
	public Boolean isAuthenticated(String broker, String accessToken) throws ApiException {
		return isAuthenticated(accessToken);
	}

	@Override
	public TokenResponse authenticateUser(UserAuthentication userAuthentication) throws Exception {

			if (userAuthentication.getAuthenticationCode() != null) {
				if (!isAuthenticated(userAuthentication.getAccessToken())) {
					return getAccessToken(userAuthentication);
				} else {
					throw new AuthException("User is already authenticated");
				}
			} else {
				throw new AuthException("Unable to Authenticate User. Please check the authentication details.");
			}
	}

	private boolean isAuthenticated(String accessToken) {
		if (accessToken == null) {
			return false;
		}
        try {
            Profile profile = getKiteConnect(new UserAuthentication()).getProfile();
			return true;
        } catch (IOException | KiteException e) {
            return false;
        }
    }

	private TokenResponse getAccessToken(UserAuthentication userAuthentication) {
		KiteConnect kiteConnect = getKiteConnect(userAuthentication);

		User user = null;
		try {
			user = kiteConnect.generateSession(userAuthentication.getAuthenticationCode(), userAuthentication.getClientApiSecret());
		} catch (KiteException e) {
			logger.error("Exception when calling LoginApi#token {} {}", e.message, e.code);
			throw new CustomApiException(HttpStatus.valueOf(e.code), e.message);
		}
		catch (IOException e) {
			logger.error("IOException when calling LoginApi#token: {}", e.getMessage());
			throw new CustomApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Server error, consider contacting admin.");
		}
		kiteConnect.setAccessToken(user.accessToken);
		kiteConnect.setPublicToken(user.publicToken);
		return new TokenResponse(kiteConnect.getAccessToken(), kiteConnect.getPublicToken());
	}

	private KiteConnect getKiteConnect(UserAuthentication userAuthentication){
		KiteConnect kiteConnect = new KiteConnect(userAuthentication.getClientApiKey());
		kiteConnect.setUserId(userAuthentication.getClientUserId());
		kiteConnect.setEnableLogging(true);
		String url = kiteConnect.getLoginURL();
		kiteConnect.setSessionExpiryHook(new SessionExpiryHook() {
			@Override
			public void sessionExpired() {
				System.out.println("session expired");
			}
		});

		kiteConnect.setAccessToken(userAuthentication.getAccessToken());
		kiteConnect.setPublicToken(userAuthentication.getPublicToken());
		return kiteConnect;
	}
}
