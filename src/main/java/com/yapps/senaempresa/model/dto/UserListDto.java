package com.yapps.senaempresa.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserListDto {
    private String userIdentification;
    private String userFirstname;
    private String userLastname;
    private String userEmail;
    private String status;
}
