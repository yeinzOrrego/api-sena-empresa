package com.yapps.senaempresa.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "type_identification")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TypeIdentification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long typeId;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 10)
    private String name;
    
}
