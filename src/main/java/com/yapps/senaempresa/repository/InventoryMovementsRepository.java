package com.yapps.senaempresa.repository;

import com.yapps.senaempresa.model.entity.InventoryMovements;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryMovementsRepository extends JpaRepository<InventoryMovements, Long> {
    List<InventoryMovements> findByProductProductIdOrderByDateCreatedDesc(Long productId);
}
