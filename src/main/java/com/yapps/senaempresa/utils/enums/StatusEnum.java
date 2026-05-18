package com.yapps.senaempresa.utils.enums;

import lombok.Getter;

@Getter
public enum StatusEnum {
    ACTIVO("A", "This state represents an active status."),
    INACTIVO("N", "This state represents an inactive status."),
    PENDIENTE("P", "This state represents a pending status."),
    APROBADO("A", "This state represents an approved status."),
    RECHAZADO("R", "This state represents a rejected status.");

    private final String value;
    private final String description;

    StatusEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }
}
