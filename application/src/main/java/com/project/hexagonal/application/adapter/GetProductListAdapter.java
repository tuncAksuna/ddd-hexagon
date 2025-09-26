package com.project.hexagonal.application.adapter;

import com.project.hexagonal.application.dto.ProductDto;
import com.project.hexagonal.application.port.input.GetProductListPort;
import com.project.hexagonal.application.port.output.ProductDataJpaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetProductListAdapter implements GetProductListPort {

    private final ProductDataJpaRepositoryPort repository;

    @Override
    public List<ProductDto> getProducts() {
        return repository.getProducts();
    }
}
