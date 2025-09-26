package com.project.hexagonal.application.port.input;

import com.project.hexagonal.application.dto.ProductDto;

public interface GetProductByIdPort {

    ProductDto getProductById(Long id);
}
