package com.yapps.senaempresa.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "plantation_inventory")
@EntityListeners(AuditingEntityListener.class)
public class PlantationInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long plantationInventoryId;

    @Column(nullable = false, length = 50, unique = true)
    private String inventoryCode;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer producedQuantity;

    @Column(nullable = false)
    private Integer availableQuantity;

    @Column(nullable = false, length = 1)
    private String status;

    @Column(nullable = false)
    private LocalDateTime productionDate;
}
