package com.yapps.senaempresa.utils.helper;

import com.yapps.senaempresa.model.entity.User;
import com.yapps.senaempresa.model.dto.TransferStockDto;
import com.yapps.senaempresa.model.entity.InventoryMovements;
import com.yapps.senaempresa.model.entity.PlantationInventory;
import com.yapps.senaempresa.model.entity.Product;
import com.yapps.senaempresa.repository.InventoryMovementsRepository;
import com.yapps.senaempresa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryServiceHelper {

    private final InventoryMovementsRepository inventoryMovementsRepository;
    private final UserRepository userRepository;

    public InventoryMovements recordMovement(Product product, TransferStockDto transferStockDto, String status) {
        User receivedUser = userRepository.findById(transferStockDto.getReceivedBy())
                .orElseThrow(() -> new IllegalArgumentException("Receiving user not found"));

        InventoryMovements movement = InventoryMovements.builder()
                .product(product)
                .receivedBy(receivedUser)
                .quantity(transferStockDto.getQuantity())
                .status(status)
                .sourceInventory(PlantationInventory.builder()
                        .plantationInventoryId(transferStockDto.getSourceInventory())
                        .build())
                .build();
        return inventoryMovementsRepository.save(movement);
    }
}
