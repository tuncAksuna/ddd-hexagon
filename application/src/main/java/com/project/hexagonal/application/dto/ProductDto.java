package com.project.hexagonal.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@Getter
public class ProductDto {

    private String name;
    private String description;
    private String stockCode;
    private BigDecimal unitPrice;
    private int quantity;
}
