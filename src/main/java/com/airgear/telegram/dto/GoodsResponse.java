package com.airgear.telegram.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal priceAmount;
    private String priceCurrency;
    private String priceType;
    private BigDecimal weekendsPriceAmount;
    private String weekendsPriceCurrency;
    private String weekendsPriceType;
    private String location;
    private String category;
}
