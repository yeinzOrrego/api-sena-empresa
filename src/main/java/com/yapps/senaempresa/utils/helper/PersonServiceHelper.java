package com.yapps.senaempresa.utils.helper;

import java.util.ArrayList;
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
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
        log.info("Validating unique keys for new user creation with Identification: {}", personDto.getUserIdentification());
        List<String> errorMessages = new ArrayList<>();

        if (existingAccount(personDto.getUserIdentification())) {
            errorMessages.add("Person with identification already exists. ");
        }

        if (accountRepository.existsByUserLogin(personDto.getUserIdentification())) {
            errorMessages.add("Person with login already exists. ");
        }

        if (accountRepository.existsByUserEmail(personDto.getUserEmail())) {
            errorMessages.add("Person with email already exists. ");
        }

        if (!errorMessages.isEmpty()) {
            log.warn("Validation failed for new user: {} rules violated", errorMessages.size());
            throw new IllegalArgumentException(String.join(" ", errorMessages));
        }
        log.info("Unique keys validation passed successfully.");
    }

    public void validateUniqueKeys(Long userId, UserDetailsDto personDto) {
        log.info("Validating unique keys for updating user ID: {}", userId);
        List<String> errorMessages = new ArrayList<>();

        if (!existingAccount(personDto.getUserIdentification())) {
            errorMessages.add("Person with identification does not exist. ");
        }

        if (accountRepository.existsByUserLoginAndUserIdNot(personDto.getUserIdentification(), userId)) {
            errorMessages.add("Person with login already exists. ");
        }

        if (accountRepository.existsByUserEmailAndUserIdNot(personDto.getUserEmail(), userId)) {
            errorMessages.add("Person with email already exists. ");
        }

        if (!errorMessages.isEmpty()) {
            log.warn("Validation failed for updating user ID {}: {} rules violated", userId, errorMessages.size());
            throw new IllegalArgumentException(String.join(" ", errorMessages));
        }
        log.info("Unique keys validation passed successfully for user ID: {}", userId);
    }

    public void validateIntegrity(Long userId, UserDetailsDto personDto) {
        log.info("Validating data integrity for updating user ID: {}", userId);
        List<String> errorMessages = new ArrayList<>();

        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Integrity validation failed: User not found with ID: {}", userId);
                    return new IllegalArgumentException("Person not found");
                });

        if (!personDto.getUserIdentification().equals(account.getUserIdentification())) {
            errorMessages.add("The identification cannot be changed.");
        }

        if (!personDto.getUserTypeIdentification().equals(account.getUserTypeIdentification())) {
            errorMessages.add("The type of identification cannot be changed.");
        }

        if (!errorMessages.isEmpty()) {
            log.warn("Data integrity validation failed for user ID {}: {} rules violated", userId, errorMessages.size());
            throw new IllegalArgumentException(String.join(" ", errorMessages));
        }
        log.info("Data integrity validation passed successfully for user ID: {}", userId);
    }

    public Account updateAccount(UserDetailsDto personDto, Account existingAccount) {
        log.info("Updating account roles/status for user ID: {}", existingAccount.getUserId());
        
        // 1. Extract incoming role IDs from the DTO, ensuring it's not null
        List<Long> incomingRoleIds = personDto.getUserRoles() != null ? personDto.getUserRoles() : List.of();
        log.info("Incoming roles count: {}", incomingRoleIds.size());

        // Extract existing roles of the account
        List<Long> existingRoleIds = existingAccount.getUserRoles().stream()
                .map(userRole -> userRole.getRole().getRoleId())
                .toList();

        // Extract new roles from the DTO
        List<Long> newRoleIds = incomingRoleIds.stream()
                .filter(role -> !existingRoleIds.contains(role))
                .toList();

        // Update the status of existing roles based on the new roles
        log.info("Updating statuses for {} existing roles", existingRoleIds.size());
        existingAccount.getUserRoles().forEach(userRole -> {
            if (incomingRoleIds.contains(userRole.getRole().getRoleId())) {
                userRole.setStatus(ACTIVE_STATUS);
            } else {
                userRole.setStatus(INACTIVE_STATUS);
            }
        });

        log.info("Found {} new role(s) to add to user ID: {}", newRoleIds.size(), existingAccount.getUserId());

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

        log.info("Roles updated successfully in memory for user ID: {}", existingAccount.getUserId());
        return existingAccount;
    }

    public Account disableAccount(UserDetailsDto personDto, Account existingAccount) {
        log.info("Disabling account globally for user ID: {}", existingAccount.getUserId());

        // Disable the account
        existingAccount.setStatus(INACTIVE_STATUS);

        // Update the associated roles to inactive
        log.info("Synchronizing incoming roles before applying global inactive status");
        existingAccount = updateAccount(personDto, existingAccount);

        log.info("Forcing inactive status on all {} roles", existingAccount.getUserRoles().size());
        existingAccount.getUserRoles().forEach(userRole -> userRole.setStatus(INACTIVE_STATUS));

        log.info("User and {} roles have been successfully marked as inactive in memory", existingAccount.getUserRoles().size());
        return existingAccount;
    }

}
