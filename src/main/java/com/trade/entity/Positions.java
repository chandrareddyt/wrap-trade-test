package com.trade.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Positions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private String broker;
    private String tradingSymbol;
    private String exchange;
    private String instrumentToken;
    private String product;
    private int quantity;
    private int overnightQuantity;
    private float multiplier;
    private float averagePrice;
    private float closePrice;
    private float lastPrice;
    @Column(name="position_value")
    private float value;
    private float pnl;
    private float unrealised;
    private float realised;
    private int buyQuantity;
    private float buyPrice;
    private float buyValue;
    private int sellQuantity;
    private float sellPrice;
    private float sellValue;
    private int dayBuyQuantity;
    private float dayBuyPrice;
    private float dayBuyValue;
    private int daySellQuantity;
    private float daySellPrice;
    private float daySellValue;
}