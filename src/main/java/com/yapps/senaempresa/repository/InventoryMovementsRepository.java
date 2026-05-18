package com.yapps.senaempresa.repository;

import com.yapps.senaempresa.model.entity.InventoryMovements;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryMovementsRepository extends JpaRepository<InventoryMovements, Long> {
    List<InventoryMovements> findByProductProductIdOrderByDateCreatedDesc(Long productId);
    Optional<InventoryMovements> findByMovementIdAndStatus(Long movementId, String status);
    @EntityGraph(attributePaths = {"receivedBy"})
    Optional<InventoryMovements> findWithUserReceivedByMovementId(Long movementId);
}
