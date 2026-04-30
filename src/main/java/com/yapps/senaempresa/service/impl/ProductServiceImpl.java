package com.yapps.senaempresa.service.impl;

import com.ada.ecosystem.core.v1.query.EcosystemRequestQuery;
import com.ada.ecosystem.core.v1.query.SearchSpecifications;
import com.ada.ecosystem.core.v1.service.EcosystemService;
import com.yapps.senaempresa.model.dto.NewProductDto;
import com.yapps.senaempresa.model.dto.ProductDetailsDto;
import com.yapps.senaempresa.model.dto.ProductListDto;
import com.yapps.senaempresa.model.dto.UpdateProductDto;
import com.ada.ecosystem.core.v1.pageable.PageDto;
import com.yapps.senaempresa.model.entity.Product;
import com.yapps.senaempresa.repository.ProductRepository;
import com.yapps.senaempresa.service.ProductService;
import com.yapps.senaempresa.utils.enums.StatusEnum;
import com.yapps.senaempresa.utils.helper.ProductServiceHelper;
import com.yapps.senaempresa.utils.mapper.ProductMapper;
import com.yapps.senaempresa.utils.response.ProcessResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends EcosystemService implements ProductService {

    private final ProductRepository productRepository;
    private final ProductServiceHelper productServiceHelper;
    private final ProductMapper productMapper;

    private static final String ACTIVE_STATUS = StatusEnum.ACTIVO.getValue();

    @Override
    @Transactional(readOnly = true)
    public PageDto<ProductListDto> getAllProducts(EcosystemRequestQuery ecosystemRequestQuery) {
        log.info("Fetching all products with pagination and filters");
        Pageable pageable = getPageable(ecosystemRequestQuery.getPage(), ecosystemRequestQuery.getSize(),
                ecosystemRequestQuery.getOrdersBy());
        SearchSpecifications<Product> especificacion = getSearchSpecifications(
                ecosystemRequestQuery.getSearchsBy());
        PageDto<ProductListDto> pageResult = productMapper.toPageDto(productRepository.findAll(especificacion, pageable));
        log.info("Successfully fetched {} products (total pages: {})", pageResult.getContent().size(), pageResult.getTotalPages());
        return pageResult;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDetailsDto getProductById(Long id) {
        log.info("Fetching product details for ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found with ID: {}", id);
                    return new IllegalArgumentException("Product not found with ID: " + id);
                });
        return productMapper.toDetailsDto(product);
    }

    @Override
    @Transactional
    public ProcessResult<Long> createProduct(NewProductDto productDto) throws IOException {
        log.info("Attempting to create a new product");

        productServiceHelper.validateUniqueKeys(productDto);

        Product entity = productMapper.toEntity(productDto);
        entity.setStatus(ACTIVE_STATUS);
        
        if (productDto.getImage() != null && !productDto.getImage().isEmpty()) {
            log.info("Processing image attachment for new product");
            entity.setAttachment(productServiceHelper.saveAttachment(productDto.getImage()));
        }

        entity = productRepository.save(entity);
        log.info("Product created successfully with ID: {}", entity.getProductId());

        return ProcessResult.<Long>builder()
                .result(entity.getProductId())
                .message("Product created successfully")
                .resultCode((long) HttpStatus.CREATED.value())
                .build();
    }

    @Override
    @Transactional
    public ProcessResult<Long> updateProduct(Long id, UpdateProductDto productDto) throws IOException {
        log.info("Attempting to update product with ID: {}", id);

        productServiceHelper.validateUniqueKeys(id, productDto);
        productServiceHelper.validateIntegrity(id, productDto);

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Failed to update: Product not found with ID: {}", id);
                    return new IllegalArgumentException("Product not found");
                });

        if (productDto.getNewAttachment() != null && !productDto.getNewAttachment().isEmpty()) {
            log.info("Updating image attachment for product ID: {}", id);
            if (productDto.getCurrentAttachmentId() != null) {
                productServiceHelper.deleteAttachment(productDto.getCurrentAttachmentId());
            }
            existingProduct.setAttachment(productServiceHelper.saveAttachment(productDto.getNewAttachment()));
        }

        productMapper.updateEntity(existingProduct, productDto);

        existingProduct = productRepository.save(existingProduct);
        log.info("Product updated successfully with ID: {}", existingProduct.getProductId());

        return ProcessResult.<Long>builder()
                .result(existingProduct.getProductId())
                .message("Product updated successfully")
                .resultCode((long) HttpStatus.OK.value())
                .build();
    }

}
