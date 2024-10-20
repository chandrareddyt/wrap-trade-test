package com.trade.entity;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class UserDetailsBroker {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String broker;
    private String brokerUserId;
    private String userName;
    private String email;
    private String userType;
    @Convert(converter = StringArrayToStringConverter.class)
    private String[] exchanges;
    @Convert(converter = StringArrayToStringConverter.class)
    private String[] products;
    @Convert(converter = StringArrayToStringConverter.class)
    private String[] orderTypes;
    private String wpUserId;
}
