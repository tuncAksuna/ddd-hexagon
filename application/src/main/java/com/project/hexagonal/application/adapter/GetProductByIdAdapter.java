package com.project.hexagonal.application.adapter;

import com.project.hexagonal.application.port.input.GetProductByIdPort;
import com.project.hexagonal.application.port.output.ProductDataJpaRepositoryPort;
import com.project.hexagonal.application.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetProductByIdAdapter implements GetProductByIdPort {

    private final ProductDataJpaRepositoryPort repository;

    @Override
    public ProductDto getProductById(Long id) {
        return repository.getProductById(id);
    }
}
