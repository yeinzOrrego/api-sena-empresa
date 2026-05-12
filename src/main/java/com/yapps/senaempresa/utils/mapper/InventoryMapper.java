package com.yapps.senaempresa.utils.mapper;

import com.ada.ecosystem.core.v1.pageable.PageDto;
import com.yapps.senaempresa.config.GlobalMapperConfig;
import com.yapps.senaempresa.model.dto.InventoryDto;
import com.yapps.senaempresa.model.dto.PlantationInventoryDto;
import com.yapps.senaempresa.model.dto.ProduceStockDto;
import com.yapps.senaempresa.model.entity.Inventory;
import com.yapps.senaempresa.model.entity.PlantationInventory;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(config = GlobalMapperConfig.class)
public interface InventoryMapper {
    InventoryDto toDto(Inventory entity);
    List<InventoryDto> toInventoryDtoList(List<Inventory> entities);

    @Mapping(target = "productId", source = "product.productId")
    PlantationInventoryDto toDto(PlantationInventory entity);
    PlantationInventory toEntity(ProduceStockDto dto);
    List<PlantationInventoryDto> toPlantationDtoList(List<PlantationInventory> entities);

    PageDto<PlantationInventoryDto> toPageDto(Page<PlantationInventory> entities);

}
