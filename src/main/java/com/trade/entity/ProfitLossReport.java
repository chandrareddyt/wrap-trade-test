package com.trade.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class ProfitLossReport {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private String broker;
//    private String tradingSymbol;
//    private String exchange;
//    private String instrumentToken;
//    private String isin;
//    private String product;
//    private int quantity;
//    private int t1Quantity;
//    private int collateralQuantity;
//    private String collateralType;
//    private float averagePrice;
//    private float lastPrice;
//    private float closePrice;
//    private float pnl;
//    private float dayChange;
//    private float dayChangePercentage;
    private double quantity;
    private String isin;
    private String scripName;
    private String tradeType;
    private String buyDate;
    private double buyAverage;
    private String sellDate;
    private double sellAverage;
    private double buyAmount;
    private double sellAmount;
}
