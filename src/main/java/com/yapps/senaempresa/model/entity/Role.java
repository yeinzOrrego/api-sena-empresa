package com.yapps.senaempresa.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "ROLE")
public class Role {

    @Id
    @Column(name = "ROLE_ID")
    private Long roleId;

    @Column(name = "ROLE_NAME")
    private String roleName;
    
    @Column(name = "STATUS")
    private String status;
    
    @Column(name = "DATE_CREATED")
    private String dateCreated;
    
    @Column(name = "USER_CREATED")
    private String userCreated;
}
