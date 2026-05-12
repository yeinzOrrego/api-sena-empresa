package com.yapps.senaempresa.service;

import com.ada.ecosystem.core.v1.pageable.PageDto;
import com.ada.ecosystem.core.v1.query.EcosystemRequestQuery;
import com.yapps.senaempresa.model.dto.NewUserDto;
import com.yapps.senaempresa.model.dto.UserDetailsDto;
import com.yapps.senaempresa.model.dto.UserListDto;
import com.yapps.senaempresa.utils.response.ProcessResult;

public interface PersonService {
    PageDto<UserListDto> getAllPersons(EcosystemRequestQuery ecosystemRequestQuery);
    UserDetailsDto getPersonById(Long userId);
    ProcessResult<Long> createPerson(NewUserDto personDto);
    ProcessResult<Long> updatePerson(Long userId, UserDetailsDto personDto);
}
