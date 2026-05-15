package com.yapps.senaempresa.service.impl;

import com.ada.ecosystem.core.v1.pageable.PageDto;
import com.ada.ecosystem.core.v1.query.EcosystemRequestQuery;
import com.ada.ecosystem.core.v1.query.SearchSpecifications;
import com.ada.ecosystem.core.v1.service.EcosystemService;
import com.yapps.senaempresa.model.dto.*;
import com.yapps.senaempresa.model.entity.Inventory;
import com.yapps.senaempresa.model.entity.PlantationInventory;
import com.yapps.senaempresa.model.entity.Product;
import com.yapps.senaempresa.repository.PlantationInventoryRepository;
import com.yapps.senaempresa.repository.ProductRepository;
import com.yapps.senaempresa.service.InventoryService;
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
    
    private final InventoryMapper mapper;
    private final InventoryServiceHelper helper;

    private static final String ACTIVE_STATUS = StatusEnum.ACTIVO.getValue();

    @Override
    @Transactional(readOnly = true)
    public PageDto<PlantationInventoryDto> getAllPlantationInventories(EcosystemRequestQuery ecosystemRequestQuery) {
        log.info("Fetching all plantation inventories with page {}, size {}", ecosystemRequestQuery.getPage(), ecosystemRequestQuery.getSize());
        Pageable pageable = getPageable(ecosystemRequestQuery.getPage(), ecosystemRequestQuery.getSize(),
                ecosystemRequestQuery.getOrdersBy());
        SearchSpecifications<PlantationInventory> specification = getSearchSpecifications(
                ecosystemRequestQuery.getSearchsBy());
        return mapper.toPageDto(plantationInventoryRepository.findAll(specification, pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlantationInventoryDto> getPlantationInventoryByProductId(Long productId) {
        return mapper.toPlantationDtoList(plantationInventoryRepository.findByProductProductId(productId));
    }

    @Override
    @Transactional
    public ProcessResult<String> produceStock(ProduceStockDto produceStockDto) {
        Product product = productRepository.findProductByProductId(produceStockDto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        boolean existingInventory = plantationInventoryRepository.existsByInventoryCode(produceStockDto.getInventoryCode());
        if (existingInventory) {
            throw new IllegalArgumentException("Inventory with code " + produceStockDto.getInventoryCode() + " already exists");
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
    public ProcessResult<String> transferToCommercial(TransferStockDto transferStockDto) {
        Product product = productRepository.findProductWithInventoryByProductId(transferStockDto.getProductId());

        PlantationInventory plantationInventory = plantationInventoryRepository.findByPlantationInventoryIdAndProduct(transferStockDto.getInventorySource(), product)
                .orElseThrow(() -> new IllegalArgumentException("No plantation inventory available for this product"));

        if (plantationInventory.getAvailableQuantity() < transferStockDto.getQuantity()) {
            throw new IllegalArgumentException("Insufficient stock in plantation. Available: " + plantationInventory.getAvailableQuantity());
        }

        // Subtract from plantation
        plantationInventory.setAvailableQuantity(plantationInventory.getAvailableQuantity() - transferStockDto.getQuantity());
        plantationInventoryRepository.save(plantationInventory);

        // Add to commercial
        Inventory commercialInventory = product.getInventory();

        commercialInventory.setStock(commercialInventory.getStock() + transferStockDto.getQuantity());

        product = productRepository.save(product);

        helper.recordMovement(product, transferStockDto.getQuantity(), transferStockDto.getReceivedBy(), transferStockDto.getDeliveredBy());

        return ProcessResult.<String>builder()
                .result("")
                .message("Stock transferred to commercial inventory successfully")
                .resultCode((long) HttpStatus.OK.value())
                .build();
    }
}
