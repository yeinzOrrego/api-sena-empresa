package com.yapps.senaempresa.utils.mapper;

import com.ada.ecosystem.core.v1.pageable.PageDto;
import org.springframework.data.domain.Page;
import com.yapps.senaempresa.config.GlobalMapperConfig;
import com.yapps.senaempresa.model.dto.NewProductDto;
import com.yapps.senaempresa.model.dto.ProductDetailsDto;
import com.yapps.senaempresa.model.dto.ProductListDto;
import com.yapps.senaempresa.model.dto.UpdateProductDto;
import com.yapps.senaempresa.model.entity.Product;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = GlobalMapperConfig.class)
public interface ProductMapper {

    @Mapping(target = "fileUrl", source = "attachment.fileUrl")
    ProductListDto toDto(Product entity);

    PageDto<ProductListDto> toPageDto(Page<Product> entities);

    @Mapping(target = "unitMeasure", source = "unitMeasure.unitId")
    @Mapping(target = "inventory.productId", source = "inventory.product.productId")
    ProductDetailsDto toDetailsDto(Product entity);

    @Mapping(target = "dateCreated", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "unitMeasure.unitId", source = "unitMeasure")
    Product toEntity(NewProductDto dto);

    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "unitMeasure.unitId", source = "unitMeasure")
    @Mapping(target = "attachment", ignore = true)
    @Mapping(target = "inventory", ignore = true)
    void updateEntity(@MappingTarget Product entity, UpdateProductDto dto);
}
