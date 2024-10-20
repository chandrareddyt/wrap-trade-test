package com.trade.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class OrderBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private String broker;
    private String exchange;
    private String product;
    private int quantity;
    private String instrumentToken;
    private String tradingSymbol;
    private String orderType;
    private String transactionType;
    private float averagePrice;
    private String exchangeOrderId;
    private String orderId;
    private String status;
    @Column(length = 500)
    private String statusMessage;
    private String orderTimestamp;
    private String exchangeTimestamp;
    private int filledQuantity;
    private int pendingQuantity;
    private float price;
    private Date createdDate;
    private Date updatedDate;
}
