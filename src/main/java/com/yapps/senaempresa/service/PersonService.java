package com.yapps.senaempresa.service;

import com.yapps.senaempresa.model.dto.NewUserDto;
import com.yapps.senaempresa.model.dto.UserDetailsDto;
import com.yapps.senaempresa.model.dto.UserListDto;
import com.yapps.senaempresa.utils.response.ProcessResult;

import java.util.List;

public interface PersonService {
    List<UserListDto> getAllPersons();
    UserDetailsDto getPersonById(Long userId);
    ProcessResult<String> createPerson(NewUserDto personDto);
    ProcessResult<String> updatePerson(Long userId, UserDetailsDto personDto);
}
