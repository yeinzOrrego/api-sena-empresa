package com.yapps.senaempresa.model.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ROLE")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    @Column(nullable = false, length = 100, unique = true)
    private String roleName;
    
    @Column(nullable = false, length = 1)
    private String status;
    
    @Column(nullable = false)
    private LocalDateTime dateCreated;
    
    @CreatedBy
    @Column(nullable = false, updatable = false)
    private Long userCreated;
}
