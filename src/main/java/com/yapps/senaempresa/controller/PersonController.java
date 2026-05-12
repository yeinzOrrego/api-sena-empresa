package com.yapps.senaempresa.controller;

import com.ada.ecosystem.core.v1.pageable.PageDto;
import com.ada.ecosystem.core.v1.query.EcosystemRequestQuery;
import com.yapps.senaempresa.model.dto.NewUserDto;
import com.yapps.senaempresa.model.dto.UserDetailsDto;
import com.yapps.senaempresa.model.dto.UserListDto;
import com.yapps.senaempresa.service.PersonService;
import com.yapps.senaempresa.utils.response.ProcessResult;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    @GetMapping
    public ResponseEntity<PageDto<UserListDto>> getAllPersons(
            @RequestBody(required = true) EcosystemRequestQuery ecosystemRequestQuery) {
        return new ResponseEntity<>(personService.getAllPersons(ecosystemRequestQuery), HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDetailsDto> getPersonById(@PathVariable Long userId) {
        return new ResponseEntity<>(personService.getPersonById(userId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ProcessResult<Long>> createPerson(@Valid @RequestBody NewUserDto personDto) {
        return new ResponseEntity<>(personService.createPerson(personDto), HttpStatus.CREATED);
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<ProcessResult<Long>> updatePerson(@PathVariable Long userId,
            @RequestBody UserDetailsDto personDto) {
        return new ResponseEntity<>(personService.updatePerson(userId, personDto), HttpStatus.OK);
    }
}
