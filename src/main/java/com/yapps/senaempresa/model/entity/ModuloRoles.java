package com.yapps.senaempresa.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "V_MODULOS_ROLES")
public class ModuloRoles implements Serializable {

    private static final long serialVersionUID = -3553404733430892112L;
    @Id
    @Column(name = "MODULOS_ROLES_ID")
    private String modulosRolesId;
    @Column(name = "MODULE_APP_ID")
    private Long moduleAppId;
    @Column(name = "APPLICATION_COMPANY_ID")
    private Long applicationCompanyId;
    @Column(name = "USER_ID")
    private Long userid;
    @Column(name = "ROLE_ID")
    private Long roleId;
    @Column(name = "MODULE_DESCRIPTION")
    private String modDescription;
    @Column(name = "MODULE_NAME")
    private String modName;
    @Column(name = "MODULE_PATH_FRONTEND")
    private String modPathFront;
    @Column(name = "MODULE_ICON")
    private String moduleIcon;
    @Column(name = "STATUS")
    private String status;
}
