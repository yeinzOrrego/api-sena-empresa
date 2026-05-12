package com.yapps.senaempresa.utils.helper;

import com.yapps.senaempresa.model.entity.Account;
import com.yapps.senaempresa.model.entity.InventoryHistory;
import com.yapps.senaempresa.model.entity.Product;
import com.yapps.senaempresa.repository.InventoryHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryServiceHelper {

    private final InventoryHistoryRepository inventoryHistoryRepository;

    public void recordHistory(Product product, Integer quantity, Long receivedBy, Long deliveredBy) {
        InventoryHistory history = InventoryHistory.builder()
                .product(product)
                .deliveredBy(Account.builder()
                .userId(deliveredBy)
                .build())
                .receivedBy(Account.builder()
                .userId(receivedBy)
                .build())
                .quantity(quantity)
                .build();
        inventoryHistoryRepository.save(history);
    }
}
