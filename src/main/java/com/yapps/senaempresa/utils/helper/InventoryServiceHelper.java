package com.yapps.senaempresa.utils.helper;

import com.yapps.senaempresa.model.entity.User;
import com.yapps.senaempresa.model.entity.InventoryMovements;
import com.yapps.senaempresa.model.entity.Product;
import com.yapps.senaempresa.repository.InventoryMovementsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryServiceHelper {

    private final InventoryMovementsRepository inventoryMovementsRepository;

    public void recordMovement(Product product, Integer quantity, Long receivedBy, Long deliveredBy) {
        InventoryMovements Movements = InventoryMovements.builder()
                .product(product)
                .deliveredBy(User.builder()
                        .userId(deliveredBy)
                        .build())
                .receivedBy(User.builder()
                        .userId(receivedBy)
                        .build())
                .quantity(quantity)
                .build();
        inventoryMovementsRepository.save(Movements);
    }
}
