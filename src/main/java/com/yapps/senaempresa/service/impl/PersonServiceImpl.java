package com.yapps.senaempresa.service.impl;

import com.ada.ecosystem.core.v1.pageable.PageDto;
import com.ada.ecosystem.core.v1.query.EcosystemRequestQuery;
import com.ada.ecosystem.core.v1.query.SearchSpecifications;
import com.ada.ecosystem.core.v1.service.EcosystemService;
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

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.yapps.senaempresa.utils.enums.StatusEnum;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonServiceImpl extends EcosystemService implements PersonService {

    private final AccountRepository accountRepository;
    private final PersonMapper personMapper;
    private final PasswordEncoder passwordEncoder;
    private final PersonServiceHelper personServiceHelper;

    private static final String INACTIVE_STATUS = StatusEnum.INACTIVO.getValue();

    @Override
    @Transactional(readOnly = true)
    public PageDto<UserListDto> getAllPersons(EcosystemRequestQuery ecosystemRequestQuery) {
        log.info("Fetching all persons with page {}, size {}", ecosystemRequestQuery.getPage(), ecosystemRequestQuery.getSize());
        Pageable pageable = getPageable(ecosystemRequestQuery.getPage(), ecosystemRequestQuery.getSize(),
                ecosystemRequestQuery.getOrdersBy());
        SearchSpecifications<Account> especificacion = getSearchSpecifications(
                ecosystemRequestQuery.getSearchsBy());
        return personMapper.toPageDto(accountRepository.findAll(especificacion, pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetailsDto getPersonById(Long userId) {
        log.info("Fetching person details by ID: {}", userId);
        return personMapper.toDetailsDto(accountRepository.findWithRolesByUserId(userId)
                .orElseThrow(() -> {
                    log.error("Person not found with ID: {}", userId);
                    return new IllegalArgumentException("Person not found");
                }));
    }

    @Override
    @Transactional
    public ProcessResult<String> createPerson(NewUserDto personDto) {
        log.info("Starting person creation process for Identification: {}", personDto.getUserIdentification());
        personServiceHelper.validateUniqueKeys(personDto);

        Account entity = personMapper.toEntity(personDto);
        entity.setUserPassword(passwordEncoder.encode(personDto.getUserIdentification()));
        entity.setStatus(INACTIVE_STATUS);
        
        entity = accountRepository.save(entity);
        log.info("Saving new person to database");

        log.info("Person created successfully with ID: {}", entity.getUserId());
        return ProcessResult.<String>builder()
                .result(entity.getUserId().toString())
                .message("User created successfully")
                .resultCode((long) HttpStatus.CREATED.value())
                .build();
    }

    @Override
    @Transactional
    public ProcessResult<String> updatePerson(Long userId, UserDetailsDto personDto) {
        log.info("Starting person update process for user ID: {}", userId);

        personServiceHelper.validateUniqueKeys(userId, personDto);
        personServiceHelper.validateIntegrity(userId, personDto);

        Account existingPerson = accountRepository.findWithRolesByUserId(userId)
                .orElseThrow(() -> {
                    log.error("Person update failed: User not found with ID: {}", userId);
                    return new IllegalArgumentException("Person not found");
                });

        personMapper.updateEntity(existingPerson, personDto);

        log.info("Updating roles and status for user ID: {}", userId);
        Account updateAccount = INACTIVE_STATUS.equals(personDto.getStatus())
                ? personServiceHelper.disableAccount(personDto, existingPerson)
                : personServiceHelper.updateAccount(personDto, existingPerson);

        updateAccount = accountRepository.save(updateAccount);

        log.info("Person updated successfully with user ID: {}", updateAccount.getUserId());
        return ProcessResult.<String>builder()
                .result(updateAccount.getUserId().toString())
                .message("User updated successfully")
                .resultCode((long) HttpStatus.OK.value())
                .build();
    }

}
