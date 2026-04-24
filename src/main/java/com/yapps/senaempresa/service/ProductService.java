package com.yapps.senaempresa.service;

import com.ada.ecosystem.core.v1.pageable.PageDto;
import com.ada.ecosystem.core.v1.query.EcosystemRequestQuery;
import com.yapps.senaempresa.model.dto.NewProductDto;
import com.yapps.senaempresa.model.dto.ProductDetailsDto;
import com.yapps.senaempresa.model.dto.ProductListDto;
import com.yapps.senaempresa.utils.response.ProcessResult;


public interface ProductService {
    PageDto<ProductListDto> getAllProducts(EcosystemRequestQuery ecosystemRequestQuery);
    ProductDetailsDto getProductById(Long id);
    ProcessResult<Long> createProduct(NewProductDto productDto);
    ProcessResult<Long> updateProduct(Long id, ProductDetailsDto productDto);
}
