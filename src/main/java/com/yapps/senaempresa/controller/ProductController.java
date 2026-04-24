package com.yapps.senaempresa.controller;

import com.ada.ecosystem.core.v1.pageable.PageDto;
import com.ada.ecosystem.core.v1.query.EcosystemRequestQuery;
import com.yapps.senaempresa.model.dto.NewProductDto;
import com.yapps.senaempresa.model.dto.ProductDetailsDto;
import com.yapps.senaempresa.model.dto.ProductListDto;
import com.yapps.senaempresa.service.ProductService;
import com.yapps.senaempresa.utils.response.ProcessResult;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<PageDto<ProductListDto>> getAllProducts(@RequestBody EcosystemRequestQuery ecosystemRequestQuery) {
        return new ResponseEntity<>(productService.getAllProducts(ecosystemRequestQuery), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailsDto> getProductById(@PathVariable Long id) {
        return new ResponseEntity<>(productService.getProductById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ProcessResult<Long>> createProduct(@Valid @RequestBody NewProductDto productDto) {
        return new ResponseEntity<>(productService.createProduct(productDto), HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ProcessResult<Long>> updateProduct(@PathVariable Long id, @RequestBody ProductDetailsDto productDto) {
        return new ResponseEntity<>(productService.updateProduct(id, productDto), HttpStatus.OK);
    }

}
