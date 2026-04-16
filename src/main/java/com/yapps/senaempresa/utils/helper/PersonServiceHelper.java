package com.yapps.senaempresa.utils.helper;

import org.springframework.stereotype.Component;
import com.yapps.senaempresa.model.entity.Account;
import com.yapps.senaempresa.model.dto.NewUserDto;
import com.yapps.senaempresa.model.dto.UserDetailsDto;
import com.yapps.senaempresa.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PersonServiceHelper {

    private final AccountRepository accountRepository;

    public boolean existingAccount(String userIdentification) {
        return accountRepository.existsByUserIdentification(userIdentification);
    }

    public void validateUniqueKeys(NewUserDto personDto) {
        StringBuilder errorMessage = new StringBuilder();

        if(existingAccount(personDto.getUserIdentification())) {
            errorMessage.append("Person with identification already exists. ");
        }

        if(accountRepository.existsByUserLogin(personDto.getUserIdentification())) {
            errorMessage.append("Person with login already exists. ");
        }

        if(accountRepository.existsByUserEmail(personDto.getUserEmail())) {
            errorMessage.append("Person with email already exists. ");
        }

        if(errorMessage.length() > 0) {
            throw new IllegalArgumentException(errorMessage.toString());
        }
    }

    public void validateUniqueKeys(Long userId, UserDetailsDto personDto) {
        StringBuilder errorMessage = new StringBuilder();

        if(existingAccount(personDto.getUserIdentification())) {
            errorMessage.append("Person with identification already exists. ");
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

        if(!personDto.getUserIdentification().equals(account.getUserIdentification())) {
            errorMessage.append("The identification cannot be changed.");
        }

        if(!personDto.getUserTypeIdentification().equals(account.getUserTypeIdentification())) {
            errorMessage.append("The type of identification cannot be changed.");
        }

        if(errorMessage.length() > 0) {
            throw new IllegalArgumentException(errorMessage.toString());
        }
    }

}
