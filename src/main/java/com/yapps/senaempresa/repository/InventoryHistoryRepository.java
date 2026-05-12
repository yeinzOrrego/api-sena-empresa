package com.yapps.senaempresa.repository;

import com.yapps.senaempresa.model.entity.InventoryHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryHistoryRepository extends JpaRepository<InventoryHistory, Long> {
    List<InventoryHistory> findByProductProductIdOrderByDateCreatedDesc(Long productId);
}
