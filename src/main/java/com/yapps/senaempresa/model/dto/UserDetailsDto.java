package com.yapps.senaempresa.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsDto {
    private Long userId;
    private String userIdentification;
    private Long userTypeIdentification;
    private String userFirstname;
    private String userLastname;
    private String userEmail;
    private String userCellular;
    private String userAddress;
    private String status;
    private List<Long> userRoles;
}
