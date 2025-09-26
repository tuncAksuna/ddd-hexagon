package com.project.hexagonal.infra.product.mapper;

import com.project.hexagonal.application.dto.ProductDto;
import com.project.hexagonal.infra.product.entity.ProductEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductDataMapper {

    public ProductDto toProductDto(ProductEntity product) {
        return ProductDto.builder()
                .name(product.getName())
                .description(product.getDescription())
                .stockCode(product.getStockCode())
                .unitPrice(product.getUnitPrice())
                .quantity(product.getQuantity())
                .build();
    }

    public List<ProductEntity> toProductEntity(List<ProductDto> productDto) {
        return productDto.stream().map(dto ->
                ProductEntity.builder()
                        .description(dto.getDescription())
                        .name(dto.getName())
                        .quantity(dto.getQuantity())
                        .stockCode(dto.getStockCode())
                        .unitPrice(dto.getUnitPrice())
                        .build()
        ).toList();
    }
}
