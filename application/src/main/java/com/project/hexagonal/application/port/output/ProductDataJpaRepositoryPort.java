package com.project.hexagonal.application.port.output;

import com.project.hexagonal.application.dto.ProductDto;

import java.util.List;

public interface ProductDataJpaRepositoryPort {

    List<ProductDto> getProducts();

    ProductDto getProductById(Long id);
}
