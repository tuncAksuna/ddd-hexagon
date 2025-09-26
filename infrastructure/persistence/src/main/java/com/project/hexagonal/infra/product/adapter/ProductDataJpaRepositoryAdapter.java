package com.project.hexagonal.infra.product.adapter;

import com.project.hexagonal.infra.product.entity.ProductEntity;
import com.project.hexagonal.infra.product.mapper.ProductDataMapper;
import com.project.hexagonal.application.port.output.ProductDataJpaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.project.hexagonal.infra.product.repository.ProductSpringDataJpaRepository;
import com.project.hexagonal.application.dto.ProductDto;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductDataJpaRepositoryAdapter implements ProductDataJpaRepositoryPort {

    private final ProductSpringDataJpaRepository productSpringDataJpaRepository;
    private final ProductDataMapper productDataMapper;

    @Override
    public List<ProductDto> getProducts() {
        return productSpringDataJpaRepository.findAll()
                .stream()
                .map(productDataMapper::toProductDto)
                .toList();
    }

    @Override
    public ProductDto getProductById(Long id) {
        ProductEntity productEntity = productSpringDataJpaRepository.findById(id)
                .orElseThrow(RuntimeException::new);
        return productDataMapper.toProductDto(productEntity);
    }
}
