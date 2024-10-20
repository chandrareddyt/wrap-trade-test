package com.trade.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.trade.response.AuthResponse;
import com.trade.response.GeneralResponse;
import com.trade.response.MarketResponse;
import com.trade.response.OrderResponse;
import com.trade.response.PortfolioResponse;
import com.trade.service.JsonParsingService;
import com.upstox.ApiException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@Autowired
	private JsonParsingService jsonParsingService;
	
	@ExceptionHandler(ApiException.class)
    public ResponseEntity<String> handleS3ServiceException(ApiException e) {
		String errMsg = jsonParsingService.extractErrorMessage(e.getResponseBody());
        return new ResponseEntity<>(errMsg, HttpStatus.valueOf(e.getCode()));
    }
	
	@ExceptionHandler(OrderException.class)
    public ResponseEntity<OrderResponse> handleOrderException(OrderException e) {
		OrderResponse response = new OrderResponse(OrderResponse.Status.FAILURE, "ORDER_ERROR", e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
	
	@ExceptionHandler(MarketException.class)
    public ResponseEntity<MarketResponse> handleMarketException(MarketException e) {
		MarketResponse response = new MarketResponse(MarketResponse.Status.FAILURE, "MARKET_ERROR", e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
	
	@ExceptionHandler(PortfolioException.class)
    public ResponseEntity<PortfolioResponse> handleportfolioException(PortfolioException e) {
		PortfolioResponse response = new PortfolioResponse(PortfolioResponse.Status.FAILURE, "PORTFOLIO_ERROR", e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
	
	@ExceptionHandler(AuthException.class)
    public ResponseEntity<AuthResponse> handleAuthException(AuthException e) {
		AuthResponse response = new AuthResponse(AuthResponse.Status.FAILURE, "Authentication_ERROR", e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
	
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> defaultException(RuntimeException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
	
	@ExceptionHandler(Exception.class)
    public ResponseEntity<GeneralResponse> defaultException(Exception e) {
		GeneralResponse response = new GeneralResponse(GeneralResponse.Status.FAILURE, "ERROR", e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
	
	@ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<GeneralResponse> unauthorizedException(UnauthorizedException e) {
		GeneralResponse response = new GeneralResponse(GeneralResponse.Status.FAILURE, "Unauthorized", e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(CustomApiException.class)
    public ResponseEntity<Object> handleCustomApiException(CustomApiException ex) {

        return new ResponseEntity<>(ex.getMessage(), ex.getStatus());
    }
}

