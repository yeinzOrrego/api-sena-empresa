package com.yapps.senaempresa.repository;

import com.yapps.senaempresa.model.entity.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @EntityGraph(attributePaths = {"attachment"})
    Page<Product> findAll(Specification<Product> specification, Pageable pageable);

    @EntityGraph(attributePaths = {"unitMeasure", "attachment", "inventory"})
    Optional<Product> findDetailedByProductId(Long id);

    @EntityGraph(attributePaths = {"inventory"})
    Product findProductWithInventoryByProductId(Long id);

    @EntityGraph(attributePaths = {"attachment"})
    Product findProductWithAttachmentByProductId(Long id);

    boolean existsByBarCode(String barCode);

    boolean existsByName(String name);

    boolean existsByBarCodeAndProductIdNot(String barCode, Long id);

    boolean existsByNameAndProductIdNot(String name, Long id);
}
