package com.yapps.senaempresa.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product")
@EntityListeners(AuditingEntityListener.class) 
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_measure_id", nullable = false)
    private UnitMeasure unitMeasure;

    @Column(nullable = false, length = 500, unique = true)
    private String name;

    @Column(precision = 12, scale = 2)
    private BigDecimal price;

    @Column(nullable = false, length = 250, unique = true)
    private String barCode;

    @Column(nullable = false, length = 1)
    private String status;

    @Column(nullable = false)
    private LocalDateTime dateCreated;

    @CreatedBy
    @Column(nullable = false, updatable = false)
    private Long userCreated;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "attachment_id")
    private Attachment attachment;

}
