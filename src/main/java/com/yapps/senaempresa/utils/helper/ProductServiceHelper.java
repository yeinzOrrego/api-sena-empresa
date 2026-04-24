package com.yapps.senaempresa.utils.helper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import com.yapps.senaempresa.model.entity.Product;
import com.yapps.senaempresa.model.dto.NewProductDto;
import com.yapps.senaempresa.model.dto.ProductDetailsDto;
import com.yapps.senaempresa.repository.ProductRepository;
import com.yapps.senaempresa.repository.UnitMeasureRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductServiceHelper {

    private final ProductRepository productRepository;
    private final UnitMeasureRepository unitMeasureRepository;

    public boolean existingProduct(String barCode) {
        return productRepository.existsByBarCode(barCode);
    }

    public void validateUniqueKeys(NewProductDto productDto) {
        log.info("Validating unique keys for new product creation with Bar Code: {}", productDto.getBarCode());
        List<String> errorMessages = new ArrayList<>();

        if (existingProduct(productDto.getBarCode())) {
            errorMessages.add("Product with bar code already exists. ");
        }

        if (productRepository.existsByName(productDto.getName())) {
            errorMessages.add("Product with name already exists. ");
        }

        if (!validateUnitMeasure(productDto.getUnitMeasure())) {
            errorMessages.add("Invalid unit measure ID supplied. ");
        }

        if (!errorMessages.isEmpty()) {
            log.warn("Validation failed for new product: {} rules violated", errorMessages.size());
            throw new IllegalArgumentException(String.join(" ", errorMessages));
        }
        log.info("Unique keys validation passed successfully.");
    }

    public void validateUniqueKeys(Long productId, ProductDetailsDto productDto) {
        log.info("Validating unique keys for updating product ID: {}", productId);
        List<String> errorMessages = new ArrayList<>();

        if (!existingProduct(productDto.getBarCode())) {
            errorMessages.add("Product with bar code does not exist. ");
        }

        if (productRepository.existsByBarCodeAndProductIdNot(productDto.getBarCode(), productId)) {
            errorMessages.add("Product with bar code already exists. ");
        }

        if (productRepository.existsByNameAndProductIdNot(productDto.getName(), productId)) {
            errorMessages.add("Product with name already exists. ");
        }

        if (!validateUnitMeasure(productDto.getUnitMeasure())) {
            errorMessages.add("Invalid unit measure ID supplied. ");
        }

        if (!errorMessages.isEmpty()) {
            log.warn("Validation failed for updating product ID {}: {} rules violated", productId, errorMessages.size());
            throw new IllegalArgumentException(String.join(" ", errorMessages));
        }
        log.info("Unique keys validation passed successfully for product ID: {}", productId);
    }

    public void validateIntegrity(Long productId, ProductDetailsDto productDto) {
        log.info("Validating data integrity for updating product ID: {}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("Integrity validation failed: Product not found with ID: {}", productId);
                    return new IllegalArgumentException("Product not found");
                });

        if (!productDto.getBarCode().equals(product.getBarCode())) {
            log.warn("Data integrity validation failed for product ID {}: The bar code cannot be changed.", productId);
            throw new IllegalArgumentException("The bar code cannot be changed.");
        }

        log.info("Data integrity validation passed successfully for product ID: {}", productId);
    }

    private boolean validateUnitMeasure(Long unitMeasureId) {
        return unitMeasureId != null && unitMeasureRepository.existsById(unitMeasureId);
    }

}
