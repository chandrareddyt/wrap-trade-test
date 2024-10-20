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
public class TradeOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String orderId;
    private String userId;
    private String broker;
    private String instrumentToken;
    private String orderType;
    private String product;
    private int quantity;
    private float price;
    private String transactionType;
    private Date createdDate;
    private Date updatedDate;
    private String status;
    private String brokerStatus;
    private float avgPrice;
    private int filledQuantity;
    private int pendingQuantity;

}