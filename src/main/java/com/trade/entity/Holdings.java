package com.trade.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Holdings {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private String broker;
    private float quantity;
    private String isin;
    private String scripName;
    private String tradeType;
    private String buyDate;
    private float buyAverage;
    private String sellDate;
    private float sellAverage;
    private float buyAmount;
    private float sellAmount;
}
