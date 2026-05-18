package com.yapps.senaempresa.controller;

import com.ada.ecosystem.core.v1.pageable.PageDto;
import com.ada.ecosystem.core.v1.query.EcosystemRequestQuery;
import com.yapps.senaempresa.model.dto.NewProductDto;
import com.yapps.senaempresa.model.dto.ProductDetailsDto;
import com.yapps.senaempresa.model.dto.ProductListDto;
import com.yapps.senaempresa.model.dto.UpdateProductDto;
import com.yapps.senaempresa.service.ProductService;
import com.yapps.senaempresa.utils.response.ProcessResult;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<PageDto<ProductListDto>> getAllProducts(
            @RequestBody EcosystemRequestQuery ecosystemRequestQuery) {
        return new ResponseEntity<>(productService.getAllProducts(ecosystemRequestQuery), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ProductDetailsDto> getProductById(@PathVariable Long id) {
        return new ResponseEntity<>(productService.getProductById(id), HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ProcessResult<Long>> createProduct(@Valid @ModelAttribute NewProductDto productDto)
            throws IOException {
        return new ResponseEntity<>(productService.createProduct(productDto), HttpStatus.CREATED);
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ProcessResult<Long>> updateProduct(@PathVariable Long id,
            @ModelAttribute UpdateProductDto productDto) throws IOException {
        return new ResponseEntity<>(productService.updateProduct(id, productDto), HttpStatus.OK);
    }

}
