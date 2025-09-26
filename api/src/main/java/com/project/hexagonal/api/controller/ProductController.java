package com.project.hexagonal.api.controller;

import com.project.hexagonal.application.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.project.hexagonal.application.port.input.GetProductListPort;
import com.project.hexagonal.application.port.input.GetProductByIdPort;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {

    private final GetProductListPort getProductListPort;
    private final GetProductByIdPort getProductByIdPort;

    @GetMapping("/get")
    @PreAuthorize("hasAnyRole('ADMIN','GUEST')")
    public ResponseEntity<List<ProductDto>> getProducts() {
        return ResponseEntity.ok(getProductListPort.getProducts());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(getProductByIdPort.getProductById(id));
    }

}
