package com.yapps.senaempresa.utils.helper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import com.yapps.senaempresa.model.entity.User;
import com.yapps.senaempresa.model.entity.ApplicationUserRole;
import com.yapps.senaempresa.model.entity.Role;
import com.yapps.senaempresa.model.dto.NewUserDto;
import com.yapps.senaempresa.model.dto.UserDetailsDto;
import com.yapps.senaempresa.repository.UserRepository;
import com.yapps.senaempresa.utils.enums.StatusEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PersonServiceHelper {

    private final UserRepository UserRepository;

    private static final String ACTIVE_STATUS = StatusEnum.ACTIVO.getValue();
    private static final String INACTIVE_STATUS = StatusEnum.INACTIVO.getValue();

    public boolean existingUser(String userIdentification) {
        return UserRepository.existsByUserIdentification(userIdentification);
    }

    public void validateUniqueKeys(NewUserDto personDto) {
        log.info("Validating unique keys for new user creation with Identification: {}", personDto.getUserIdentification());
        List<String> errorMessages = new ArrayList<>();

        if (existingUser(personDto.getUserIdentification())) {
            errorMessages.add("Person with identification already exists. ");
        }

        if (UserRepository.existsByUserLogin(personDto.getUserIdentification())) {
            errorMessages.add("Person with login already exists. ");
        }

        if (UserRepository.existsByUserEmail(personDto.getUserEmail())) {
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

        if (!existingUser(personDto.getUserIdentification())) {
            errorMessages.add("Person with identification does not exist. ");
        }

        if (UserRepository.existsByUserLoginAndUserIdNot(personDto.getUserIdentification(), userId)) {
            errorMessages.add("Person with login already exists. ");
        }

        if (UserRepository.existsByUserEmailAndUserIdNot(personDto.getUserEmail(), userId)) {
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

        User User = UserRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Integrity validation failed: User not found with ID: {}", userId);
                    return new IllegalArgumentException("Person not found");
                });

        if (!personDto.getUserIdentification().equals(User.getUserIdentification())) {
            errorMessages.add("The identification cannot be changed.");
        }

        if (!personDto.getUserTypeIdentification().equals(User.getTypeIdentification().getTypeId())) {
            errorMessages.add("The type of identification cannot be changed.");
        }

        if (!errorMessages.isEmpty()) {
            log.warn("Data integrity validation failed for user ID {}: {} rules violated", userId, errorMessages.size());
            throw new IllegalArgumentException(String.join(" ", errorMessages));
        }
        log.info("Data integrity validation passed successfully for user ID: {}", userId);
    }

    public User updateUser(UserDetailsDto personDto, User existingUser) {
        log.info("Updating User roles/status for user ID: {}", existingUser.getUserId());
        
        // 1. Extract incoming role IDs from the DTO, ensuring it's not null
        List<Long> incomingRoleIds = personDto.getUserRoles() != null ? personDto.getUserRoles() : List.of();
        log.info("Incoming roles count: {}", incomingRoleIds.size());

        // Extract existing roles of the User
        List<Long> existingRoleIds = existingUser.getUserRoles().stream()
                .map(userRole -> userRole.getRole().getRoleId())
                .toList();

        // Extract new roles from the DTO
        List<Long> newRoleIds = incomingRoleIds.stream()
                .filter(role -> !existingRoleIds.contains(role))
                .toList();

        // Update the status of existing roles based on the new roles
        log.info("Updating statuses for {} existing roles", existingRoleIds.size());
        existingUser.getUserRoles().forEach(userRole -> {
            if (incomingRoleIds.contains(userRole.getRole().getRoleId())) {
                userRole.setStatus(ACTIVE_STATUS);
            } else {
                userRole.setStatus(INACTIVE_STATUS);
            }
        });

        log.info("Found {} new role(s) to add to user ID: {}", newRoleIds.size(), existingUser.getUserId());

        // Add new roles that are not currently associated with the User
        List<ApplicationUserRole> newRoles = newRoleIds.stream()
                .map(roleId -> ApplicationUserRole.builder()
                        .user(existingUser)
                        .role(Role.builder()
                        .roleId(roleId)
                        .build())
                        .dateCreated(LocalDateTime.now())
                        .status(ACTIVE_STATUS)
                        .build())
                .toList();

        existingUser.getUserRoles().addAll(newRoles);

        log.info("Roles updated successfully in memory for user ID: {}", existingUser.getUserId());
        return existingUser;
    }

    public User disableUser(UserDetailsDto personDto, User existingUser) {
        log.info("Disabling User globally for user ID: {}", existingUser.getUserId());

        // Disable the User
        existingUser.setStatus(INACTIVE_STATUS);

        // Update the associated roles to inactive
        log.info("Synchronizing incoming roles before applying global inactive status");
        existingUser = updateUser(personDto, existingUser);

        log.info("Forcing inactive status on all {} roles", existingUser.getUserRoles().size());
        existingUser.getUserRoles().forEach(userRole -> userRole.setStatus(INACTIVE_STATUS));

        log.info("User and {} roles have been successfully marked as inactive in memory", existingUser.getUserRoles().size());
        return existingUser;
    }

}
