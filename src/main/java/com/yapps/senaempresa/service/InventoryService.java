package com.yapps.senaempresa.service;

import java.util.List;

import com.ada.ecosystem.core.v1.pageable.PageDto;
import com.ada.ecosystem.core.v1.query.EcosystemRequestQuery;
import com.yapps.senaempresa.model.dto.PlantationInventoryDto;
import com.yapps.senaempresa.model.dto.ProduceStockDto;
import com.yapps.senaempresa.model.dto.TransferStockDto;
import com.yapps.senaempresa.utils.response.ProcessResult;

public interface InventoryService {
    PageDto<PlantationInventoryDto> getAllPlantationInventories(EcosystemRequestQuery ecosystemRequestQuery);
    List<PlantationInventoryDto> getPlantationInventoryByProductId(Long productId);
    ProcessResult<String> produceStock(ProduceStockDto produceStockDto);
    ProcessResult<String> transferToCommercial(TransferStockDto transferStockDto);
}
