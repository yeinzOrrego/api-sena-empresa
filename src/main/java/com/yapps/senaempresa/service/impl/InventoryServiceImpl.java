package com.yapps.senaempresa.service.impl;

import com.ada.ecosystem.core.v1.pageable.PageDto;
import com.ada.ecosystem.core.v1.query.EcosystemRequestQuery;
import com.ada.ecosystem.core.v1.query.SearchSpecifications;
import com.ada.ecosystem.core.v1.service.EcosystemService;
import com.yapps.senaempresa.model.dto.*;
import com.yapps.senaempresa.model.entity.Inventory;
import com.yapps.senaempresa.model.entity.PlantationInventory;
import com.yapps.senaempresa.model.entity.Product;
import com.yapps.senaempresa.model.entity.User;
import com.yapps.senaempresa.model.entity.InventoryMovements;
import com.yapps.senaempresa.repository.InventoryMovementsRepository;
import com.yapps.senaempresa.repository.PlantationInventoryRepository;
import com.yapps.senaempresa.repository.ProductRepository;
import com.yapps.senaempresa.service.InventoryService;
import com.yapps.senaempresa.service.NotificationService;
import com.yapps.senaempresa.utils.enums.StatusEnum;
import com.yapps.senaempresa.utils.helper.InventoryServiceHelper;
import com.yapps.senaempresa.utils.mapper.InventoryMapper;
import com.yapps.senaempresa.utils.response.ProcessResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl extends EcosystemService implements InventoryService {

    private final PlantationInventoryRepository plantationInventoryRepository;
    private final ProductRepository productRepository;
    private final InventoryMovementsRepository inventoryMovementsRepository;

    private final InventoryMapper mapper;
    private final InventoryServiceHelper helper;
    private final NotificationService notificationService;

    private static final String ACTIVE_STATUS = StatusEnum.ACTIVO.getValue();
    private static final String PENDING_STATUS = StatusEnum.PENDIENTE.getValue();
    private static final String APPROVED_STATUS = StatusEnum.APROBADO.getValue();
    private static final String REJECTED_STATUS = StatusEnum.RECHAZADO.getValue();

    @Override
    @Transactional(readOnly = true)
    public PageDto<PlantationInventoryDto> getAllPlantationInventories(EcosystemRequestQuery ecosystemRequestQuery) {
        log.info("Fetching all plantation inventories with page {}, size {}", ecosystemRequestQuery.getPage(),
                ecosystemRequestQuery.getSize());
        Pageable pageable = getPageable(ecosystemRequestQuery.getPage(), ecosystemRequestQuery.getSize(),
                ecosystemRequestQuery.getOrdersBy());
        SearchSpecifications<PlantationInventory> specification = getSearchSpecifications(
                ecosystemRequestQuery.getSearchsBy());
        return mapper.toPageDto(plantationInventoryRepository.findAll(specification, pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlantationInventoryDto> getPlantationInventoryByProductId(Long productId) {
        return mapper.toPlantationDtoList(
                plantationInventoryRepository.findByProductProductIdOrderByProductionDateDesc(productId));
    }

    @Override
    @Transactional
    public ProcessResult<String> produceStock(ProduceStockDto produceStockDto) {
        Product product = productRepository.findById(produceStockDto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        boolean existingInventory = plantationInventoryRepository
                .existsByInventoryCode(produceStockDto.getInventoryCode());
        if (existingInventory) {
            throw new IllegalArgumentException(
                    "Inventory with code " + produceStockDto.getInventoryCode() + " already exists");
        }

        PlantationInventory plantationInventory = mapper.toEntity(produceStockDto);
        plantationInventory.setStatus(ACTIVE_STATUS);
        plantationInventory.setProduct(product);

        plantationInventory = plantationInventoryRepository.save(plantationInventory);

        return ProcessResult.<String>builder()
                .result(plantationInventory.getInventoryCode())
                .message("Inventory " + plantationInventory.getInventoryCode() + " created successfully")
                .resultCode((long) HttpStatus.CREATED.value())
                .build();
    }

    @Override
    @Transactional
    public ProcessResult<Long> requestTransfer(TransferStockDto transferStockDto) {
        Product product = productRepository.findProductWithInventoryByProductId(transferStockDto.getProductId());

        PlantationInventory plantationInventory = plantationInventoryRepository
                .findByPlantationInventoryIdAndProduct(transferStockDto.getSourceInventory(), product)
                .orElseThrow(() -> new IllegalArgumentException("No plantation inventory available for this product"));

        if (plantationInventory.getAvailableQuantity() < transferStockDto.getQuantity()) {
            throw new IllegalArgumentException(
                    "Insufficient stock in plantation. Available: " + plantationInventory.getAvailableQuantity());
        }

        // Registrar el movimiento como PENDIENTE
        InventoryMovements movement = helper.recordMovement(product, transferStockDto, PENDING_STATUS);

        // Emitir evento por WebSocket
        notificationService.notifyTransferRequest(mapper.toDto(movement));

        return ProcessResult.<Long>builder()
                .result(movement.getMovementId())
                .message("Transfer request created and pending approval")
                .resultCode((long) HttpStatus.CREATED.value())
                .build();
    }

    @Override
    @Transactional
    public ProcessResult<Long> resolveTransfer(ResolveTransferDto resolveTransferDto) {
        InventoryMovements movement = inventoryMovementsRepository
                .findByMovementIdAndStatus(resolveTransferDto.getMovementId(), PENDING_STATUS)
                .orElseThrow(() -> new IllegalArgumentException("Transfer request not found or already resolved"));

        movement.setDeliveredBy(User.builder().userId(resolveTransferDto.getDeliveredBy()).build());

        if (REJECTED_STATUS.equalsIgnoreCase(resolveTransferDto.getStatus())) {
            movement.setStatus(REJECTED_STATUS);
            inventoryMovementsRepository.save(movement);
            return ProcessResult.<Long>builder()
                    .result(movement.getMovementId())
                    .message("Transfer request rejected")
                    .resultCode((long) HttpStatus.OK.value())
                    .build();
        }

        if (APPROVED_STATUS.equalsIgnoreCase(resolveTransferDto.getStatus())) {
            Product product = movement.getProduct();
            // Buscar inventario de la plantación
            PlantationInventory plantationInventory = plantationInventoryRepository
                    .findByPlantationInventoryIdAndProduct(movement.getSourceInventory().getPlantationInventoryId(),
                            product)
                    .orElseThrow(
                            () -> new IllegalArgumentException("No plantation inventory available for this product"));

            if (plantationInventory.getAvailableQuantity() < movement.getQuantity()) {
                throw new IllegalArgumentException(
                        "Insufficient stock in plantation. Available: " + plantationInventory.getAvailableQuantity());
            }

            // Restar en plantación
            plantationInventory
                    .setAvailableQuantity(plantationInventory.getAvailableQuantity() - movement.getQuantity());
            plantationInventoryRepository.save(plantationInventory);

            // Sumar en comercial
            Inventory commercialInventory = product.getInventory();
            commercialInventory.setStock(commercialInventory.getStock() + movement.getQuantity());
            productRepository.save(product);

            movement.setStatus(APPROVED_STATUS);
            inventoryMovementsRepository.save(movement);

            return ProcessResult.<Long>builder()
                    .result(movement.getMovementId())
                    .message("Transfer request approved and stock updated")
                    .resultCode((long) HttpStatus.OK.value())
                    .build();
        }

        throw new IllegalArgumentException("Invalid status provided for resolution");
    }
}
