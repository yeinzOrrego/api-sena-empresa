package com.yapps.senaempresa.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserListDto {
    private String userIdentification;
    private String userFirstname;
    private String userLastname;
    private String userEmail;
    private String status;
}
