package com.yapps.senaempresa.model.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "V_MODULO_OPCIONES")
public class ModuloOpciones implements Serializable {
    private static final long serialVersionUID = -858486563310709543L;

    @Id
    @Column(name = "OPTION_APP_ID")
    private Long optionAppId;

    @Column(name = "MODULE_APP_ID")
    private Long moduleAppId;

    @Column(name = "OPTION_NAME")
    private String optionName;

    @Column(name = "OPTION_PATH_FRONTEND")
    private String opPathFront;

    @Column(name = "OPTION_ICON")
    private String opIcon;

    @Column(name = "OPTION_PARENT")
    private Long opParent;

    @Column(name = "OPTION_ORDER")
    private Integer optionOrder;

    @Column(name = "APP_COMPANY_ID")
    private Long appCompanyId;

    @Column(name = "CAN_CREATE")
    private Boolean canCreate;

    @Column(name = "CAN_READ")
    private Boolean canRead;

    @Column(name = "CAN_UPDATE")
    private Boolean canUpdate;

    @Column(name = "CAN_DELETE")
    private Boolean canDelete;

    @Column(name = "CAN_DOWNLOAD")
    private Boolean canDownload;

    @Column(name = "CAN_UPLOAD")
    private Boolean canUpload;
}
