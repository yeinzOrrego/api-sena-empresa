package com.yapps.senaempresa.repository;

import com.yapps.senaempresa.model.entity.PlantationInventory;
import com.yapps.senaempresa.model.entity.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlantationInventoryRepository extends JpaRepository<PlantationInventory, Long>, JpaSpecificationExecutor<PlantationInventory> {
    List<PlantationInventory> findByProductProductIdOrderByProductionDateDesc(Long productId);
    Optional<PlantationInventory> findByPlantationInventoryIdAndProduct(Long plantationInventoryId, Product product);

    @Override
    @EntityGraph(attributePaths = {"product"})
    Page<PlantationInventory> findAll(Specification<PlantationInventory> spec, Pageable pageable);

    boolean existsByInventoryCode(String inventoryCode);
}
