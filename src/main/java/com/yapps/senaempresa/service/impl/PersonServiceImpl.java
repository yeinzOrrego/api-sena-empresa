package com.yapps.senaempresa.service.impl;

import com.ada.ecosystem.core.v1.pageable.PageDto;
import com.ada.ecosystem.core.v1.query.EcosystemRequestQuery;
import com.ada.ecosystem.core.v1.query.SearchSpecifications;
import com.ada.ecosystem.core.v1.service.EcosystemService;
import com.yapps.senaempresa.model.dto.NewUserDto;
import com.yapps.senaempresa.model.dto.UserDetailsDto;
import com.yapps.senaempresa.model.dto.UserListDto;
import com.yapps.senaempresa.model.entity.User;
import com.yapps.senaempresa.repository.UserRepository;
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

    private final UserRepository UserRepository;
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
        SearchSpecifications<User> especificacion = getSearchSpecifications(
                ecosystemRequestQuery.getSearchsBy());
        return personMapper.toPageDto(UserRepository.findAll(especificacion, pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetailsDto getPersonById(Long userId) {
        log.info("Fetching person details by ID: {}", userId);
        return personMapper.toDetailsDto(UserRepository.findWithRolesByUserId(userId)
                .orElseThrow(() -> {
                    log.error("Person not found with ID: {}", userId);
                    return new IllegalArgumentException("Person not found");
                }));
    }

    @Override
    @Transactional
    public ProcessResult<Long> createPerson(NewUserDto personDto) {
        log.info("Starting person creation process for Identification: {}", personDto.getUserIdentification());
        personServiceHelper.validateUniqueKeys(personDto);

        User entity = personMapper.toEntity(personDto);
        entity.setUserPassword(passwordEncoder.encode(personDto.getUserIdentification()));
        entity.setStatus(INACTIVE_STATUS);
        
        entity = UserRepository.save(entity);
        log.info("Saving new person to database");

        log.info("Person created successfully with ID: {}", entity.getUserId());
        return ProcessResult.<Long>builder()
                .result(entity.getUserId())
                .message("User created successfully")
                .resultCode((long) HttpStatus.CREATED.value())
                .build();
    }

    @Override
    @Transactional
    public ProcessResult<Long> updatePerson(Long userId, UserDetailsDto personDto) {
        log.info("Starting person update process for user ID: {}", userId);

        personServiceHelper.validateUniqueKeys(userId, personDto);
        personServiceHelper.validateIntegrity(userId, personDto);

        User existingPerson = UserRepository.findWithRolesByUserId(userId)
                .orElseThrow(() -> {
                    log.error("Person update failed: User not found with ID: {}", userId);
                    return new IllegalArgumentException("Person not found");
                });

        personMapper.updateEntity(existingPerson, personDto);

        log.info("Updating roles and status for user ID: {}", userId);
        User updateUser = INACTIVE_STATUS.equals(personDto.getStatus())
                ? personServiceHelper.disableUser(personDto, existingPerson)
                : personServiceHelper.updateUser(personDto, existingPerson);

        updateUser = UserRepository.save(updateUser);

        log.info("Person updated successfully with user ID: {}", updateUser.getUserId());
        return ProcessResult.<Long>builder()
                .result(updateUser.getUserId())
                .message("User updated successfully")
                .resultCode((long) HttpStatus.OK.value())
                .build();
    }

}
