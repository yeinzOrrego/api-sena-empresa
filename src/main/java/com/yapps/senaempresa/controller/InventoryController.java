package com.yapps.senaempresa.controller;

import com.ada.ecosystem.core.v1.pageable.PageDto;
import com.ada.ecosystem.core.v1.query.EcosystemRequestQuery;
import com.yapps.senaempresa.model.dto.PlantationInventoryDto;
import com.yapps.senaempresa.model.dto.ProduceStockDto;
import com.yapps.senaempresa.model.dto.ResolveTransferDto;
import com.yapps.senaempresa.model.dto.TransferStockDto;
import com.yapps.senaempresa.service.InventoryService;
import com.yapps.senaempresa.utils.response.ProcessResult;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventories")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/plantation")
    @PreAuthorize("hasAnyRole('PLANTACION', 'PUNTO DE VENTA')")
    public ResponseEntity<PageDto<PlantationInventoryDto>> getAllPlantationInventories(
            @RequestBody(required = true) EcosystemRequestQuery ecosystemRequestQuery) {
        return new ResponseEntity<>(inventoryService.getAllPlantationInventories(ecosystemRequestQuery), HttpStatus.OK);
    }

    @GetMapping("/plantation/product/{productId}")
    @PreAuthorize("hasAnyRole('PLANTACION', 'PUNTO DE VENTA')")
    public ResponseEntity<List<PlantationInventoryDto>> getPlantationInventory(@PathVariable Long productId) {
        return new ResponseEntity<>(inventoryService.getPlantationInventoryByProductId(productId), HttpStatus.OK);
    }

    @PostMapping("/produce")
    @PreAuthorize("hasRole('PLANTACION')")
    public ResponseEntity<ProcessResult<String>> produceStock(@Valid @RequestBody ProduceStockDto produceStockDto) {
        return new ResponseEntity<>(inventoryService.produceStock(produceStockDto), HttpStatus.OK);
    }

    @PostMapping("/transfer/request")
    @PreAuthorize("hasRole('PUNTO DE VENTA')")
    public ResponseEntity<ProcessResult<Long>> requestTransfer(@Valid @RequestBody TransferStockDto transferStockDto) {
        return new ResponseEntity<>(inventoryService.requestTransfer(transferStockDto), HttpStatus.CREATED);
    }

    @PostMapping("/transfer/resolve")
    @PreAuthorize("hasRole('PLANTACION')")
    public ResponseEntity<ProcessResult<Long>> resolveTransfer(@Valid @RequestBody ResolveTransferDto resolveTransferDto) {
        return new ResponseEntity<>(inventoryService.resolveTransfer(resolveTransferDto), HttpStatus.OK);
    }
}
