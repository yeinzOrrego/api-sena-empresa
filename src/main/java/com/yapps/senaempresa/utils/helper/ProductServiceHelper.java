package com.yapps.senaempresa.utils.helper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.yapps.senaempresa.model.entity.Product;
import com.yapps.senaempresa.model.entity.Attachment;
import com.yapps.senaempresa.model.entity.Inventory;
import com.yapps.senaempresa.model.dto.NewProductDto;
import com.yapps.senaempresa.model.dto.UpdateProductDto;
import com.yapps.senaempresa.repository.AttachmentRepository;
import com.yapps.senaempresa.repository.ProductRepository;
import com.yapps.senaempresa.repository.UnitMeasureRepository;
import com.yapps.senaempresa.service.StorageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductServiceHelper {

    private final ProductRepository productRepository;
    private final UnitMeasureRepository unitMeasureRepository;
    private final AttachmentRepository attachmentRepository;
    private final StorageService storageService;

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

    public void validateUniqueKeys(Long productId, UpdateProductDto productDto) {
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
            log.warn("Validation failed for updating product ID {}: {} rules violated", productId,
                    errorMessages.size());
            throw new IllegalArgumentException(String.join(" ", errorMessages));
        }
        log.info("Unique keys validation passed successfully for product ID: {}", productId);
    }

    public void validateIntegrity(Long productId, UpdateProductDto productDto) {
        log.info("Validating data integrity for updating product ID: {}", productId);

        List<String> errorMessages = new ArrayList<>();
        
        Product product = productRepository.findProductWithAttachmentByProductId(productId);

        if (!productDto.getBarCode().equals(product.getBarCode())) {
            log.warn("Data integrity validation failed for product ID {}: The bar code cannot be changed.", productId);
            errorMessages.add("The bar code cannot be changed.");
        }

        Long existingAttachmentId = product.getAttachment() != null ? product.getAttachment().getAttachmentId() : null;
        if ((productDto.getCurrentAttachmentId() != null && !productDto.getCurrentAttachmentId().equals(existingAttachmentId)) ||
            (productDto.getCurrentAttachmentId() == null && existingAttachmentId != null)) {
            log.warn("Data integrity validation failed for product ID {}: The send attachment is different.", productId);
            errorMessages.add("The send attachment is different from the current one.");
        }

        if (!errorMessages.isEmpty()) {
            log.warn("Data integrity validation failed for product ID {}: {} rules violated", productId,
                    errorMessages.size());
            throw new IllegalArgumentException(String.join(" ", errorMessages));
        }

        log.info("Data integrity validation passed successfully for product ID: {}", productId);
    }

    public Attachment saveAttachment(MultipartFile file) throws IOException {
        Map<String, String> attachmentInfo = Map.of();

        log.info("Uploading file: {}", file.getOriginalFilename());
        try {
            attachmentInfo = storageService.uploadFile(file);
        } catch (IOException e) {
            log.error("Failed to upload file {}: {}", file.getOriginalFilename(), e.getMessage());
            throw new IOException("Failed to upload file", e);
        }

        return Attachment.builder()
                .fileName(attachmentInfo.get("objectName"))
                .fileType(file.getContentType())
                .uploadDate(LocalDateTime.now())
                .fileUrl(attachmentInfo.get("url"))
                .build();
    }

    public void deleteAttachment(Long attachmentId) throws IOException {
        log.info("Deleting attachment with ID: {}", attachmentId);

        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> {
                    log.error("Attachment not found with ID: {}", attachmentId);
                    return new IllegalArgumentException("Attachment not found");
                });

        try {
            storageService.deleteFile(attachment.getFileName());
            attachmentRepository.delete(attachment);
            log.info("Attachment with ID {} deleted successfully", attachmentId);
        } catch (IOException e) {
            log.error("Failed to delete attachment with ID {}: {}", attachmentId, e.getMessage());
            throw new IOException("Failed to delete attachment", e);
        }
    }

    private boolean validateUnitMeasure(Long unitMeasureId) {
        return unitMeasureId != null && unitMeasureRepository.existsById(unitMeasureId);
    }

    public Inventory newInventory(Product product) {
        return Inventory.builder()
                .product(product)
                .stock(0)
                .build();
    }

}
