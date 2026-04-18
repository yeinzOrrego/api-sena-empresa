package com.yapps.senaempresa.utils.helper;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;
import com.yapps.senaempresa.model.entity.Account;
import com.yapps.senaempresa.model.entity.ApplicationUserRole;
import com.yapps.senaempresa.model.entity.Role;
import com.yapps.senaempresa.model.dto.NewUserDto;
import com.yapps.senaempresa.model.dto.UserDetailsDto;
import com.yapps.senaempresa.repository.AccountRepository;
import com.yapps.senaempresa.utils.enums.StatusEnum;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PersonServiceHelper {

    private final AccountRepository accountRepository;

    private static final String ACTIVE_STATUS = StatusEnum.ACTIVO.getValue();
    private static final String INACTIVE_STATUS = StatusEnum.INACTIVO.getValue();

    public boolean existingAccount(String userIdentification) {
        return accountRepository.existsByUserIdentification(userIdentification);
    }

    public void validateUniqueKeys(NewUserDto personDto) {
        StringBuilder errorMessage = new StringBuilder();

        if (existingAccount(personDto.getUserIdentification())) {
            errorMessage.append("Person with identification already exists. ");
        }

        if (accountRepository.existsByUserLogin(personDto.getUserIdentification())) {
            errorMessage.append("Person with login already exists. ");
        }

        if (accountRepository.existsByUserEmail(personDto.getUserEmail())) {
            errorMessage.append("Person with email already exists. ");
        }

        if (errorMessage.length() > 0) {
            throw new IllegalArgumentException(errorMessage.toString());
        }
    }

    public void validateUniqueKeys(Long userId, UserDetailsDto personDto) {
        StringBuilder errorMessage = new StringBuilder();

        if (!existingAccount(personDto.getUserIdentification())) {
            errorMessage.append("Person with identification does not exist. ");
        }

        if (accountRepository.existsByUserLoginAndUserIdNot(personDto.getUserIdentification(), userId)) {
            errorMessage.append("Person with login already exists. ");
        }

        if (accountRepository.existsByUserEmailAndUserIdNot(personDto.getUserEmail(), userId)) {
            errorMessage.append("Person with email already exists. ");
        }

        if (errorMessage.length() > 0) {
            throw new IllegalArgumentException(errorMessage.toString());
        }
    }

    public void validateIntegrity(Long userId, UserDetailsDto personDto) {
        StringBuilder errorMessage = new StringBuilder();

        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Person not found"));

        if (!personDto.getUserIdentification().equals(account.getUserIdentification())) {
            errorMessage.append("The identification cannot be changed.");
        }

        if (!personDto.getUserTypeIdentification().equals(account.getUserTypeIdentification())) {
            errorMessage.append("The type of identification cannot be changed.");
        }

        if (errorMessage.length() > 0) {
            throw new IllegalArgumentException(errorMessage.toString());
        }
    }

    public Account updateAccount(UserDetailsDto personDto, Account existingAccount) {
        // 1. Extract incoming role IDs from the DTO, ensuring it's not null
        List<Long> incomingRoleIds = personDto.getUserRoles() != null ? personDto.getUserRoles() : List.of();

        // Extract existing roles of the account
        List<Long> existingRoleIds = existingAccount.getUserRoles().stream()
                .map(userRole -> userRole.getRole().getRoleId())
                .toList();

        // Extract new roles from the DTO
        List<Long> newRoleIds = incomingRoleIds.stream()
                .filter(role -> !existingRoleIds.contains(role))
                .toList();

        // Update the status of existing roles based on the new roles
        existingAccount.getUserRoles().forEach(userRole -> {
            if (incomingRoleIds.contains(userRole.getRole().getRoleId())) {
                userRole.setStatus(ACTIVE_STATUS);
            } else {
                userRole.setStatus(INACTIVE_STATUS);
            }
        });

        // Add new roles that are not currently associated with the account
        List<ApplicationUserRole> newRoles = newRoleIds.stream()
                .map(roleId -> ApplicationUserRole.builder()
                        .user(existingAccount)
                        .role(Role.builder()
                        .roleId(roleId)
                        .build())
                        .dateCreated(new Date())
                        .userCreated(personDto.getUserCreated())
                        .status(ACTIVE_STATUS)
                        .build())
                .toList();

        existingAccount.getUserRoles().addAll(newRoles);

        return existingAccount;
    }

    public Account disableAccount(UserDetailsDto personDto, Account existingAccount) {
        // Disable the account
        existingAccount.setStatus(INACTIVE_STATUS);

        // Update the associated roles to inactive
        existingAccount = updateAccount(personDto, existingAccount);
        existingAccount.getUserRoles().forEach(userRole -> userRole.setStatus(INACTIVE_STATUS));

        return existingAccount;
    }

}
