package com.trade.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean("UpstoxPortfolioModelMapper")
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
