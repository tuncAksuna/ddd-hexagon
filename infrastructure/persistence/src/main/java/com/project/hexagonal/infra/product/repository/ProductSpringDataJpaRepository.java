package com.project.hexagonal.infra.product.repository;

import com.project.hexagonal.infra.product.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductSpringDataJpaRepository extends JpaRepository<ProductEntity, Long> {

}
