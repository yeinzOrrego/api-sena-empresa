package com.yapps.senaempresa.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "inventory_movements")
@EntityListeners(AuditingEntityListener.class)
public class InventoryMovements {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long movementId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivered_by", nullable = true)
    private User deliveredBy;

    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "received_by", nullable = false)
    private User receivedBy;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, length = 1)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "plantation_inventory_id", nullable = false)
    private PlantationInventory sourceInventory;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreated;
}
