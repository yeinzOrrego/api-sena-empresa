package com.yapps.senaempresa.service.impl;

import com.yapps.senaempresa.model.dto.NewUserDto;
import com.yapps.senaempresa.model.dto.UserDetailsDto;
import com.yapps.senaempresa.model.dto.UserListDto;
import com.yapps.senaempresa.model.entity.Account;
import com.yapps.senaempresa.repository.AccountRepository;
import com.yapps.senaempresa.service.PersonService;
import com.yapps.senaempresa.utils.helper.PersonServiceHelper;
import com.yapps.senaempresa.utils.mapper.PersonMapper;
import com.yapps.senaempresa.utils.response.ProcessResult;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {

    private final AccountRepository accountRepository;
    private final PersonMapper personMapper;
    private final PasswordEncoder passwordEncoder;
    private final PersonServiceHelper personServiceHelper;

    @Override
    @Transactional(readOnly = true)
    public List<UserListDto> getAllPersons() {
        return personMapper.toDtoList(accountRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetailsDto getPersonById(Long userId) {
        return personMapper.toDetailsDto(accountRepository.findWithRolesByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Person not found")));
    }

    @Override
    @Transactional
    public ProcessResult<String> createPerson(NewUserDto personDto) {
        personServiceHelper.validateUniqueKeys(personDto);

        Account entity = personMapper.toEntity(personDto);
        entity.setUserPassword(passwordEncoder.encode(personDto.getUserPassword()));
        entity.setStatus("N");
        entity = accountRepository.save(entity);

        return ProcessResult.<String>builder()
                .result(entity.getUserId().toString())
                .message("User created successfully")
                .resultCode((long) HttpStatus.CREATED.value())
                .build();
    }

    @Override
    @Transactional
    public ProcessResult<String> updatePerson(Long userId, UserDetailsDto personDto) {

        personServiceHelper.validateUniqueKeys(userId, personDto);
        personServiceHelper.validateIntegrity(userId, personDto);

        Account existingPerson = accountRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Person not found"));

        personMapper.updateEntity(existingPerson, personDto);
        
        Account savedEntity = accountRepository.save(existingPerson);

        return ProcessResult.<String>builder()
                .result(savedEntity.getUserId().toString())
                .message("User updated successfully")
                .resultCode((long) HttpStatus.OK.value())
                .build();
    }

}
