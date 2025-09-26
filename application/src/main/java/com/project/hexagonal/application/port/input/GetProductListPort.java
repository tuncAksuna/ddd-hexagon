package com.project.hexagonal.application.port.input;

import com.project.hexagonal.application.dto.ProductDto;

import java.util.List;

public interface GetProductListPort {

    List<ProductDto> getProducts();
}
