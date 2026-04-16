package com.yapps.senaempresa.controller;

import com.yapps.senaempresa.model.dto.NewUserDto;
import com.yapps.senaempresa.model.dto.UserDetailsDto;
import com.yapps.senaempresa.model.dto.UserListDto;
import com.yapps.senaempresa.service.PersonService;
import com.yapps.senaempresa.utils.response.ProcessResult;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    @GetMapping
    public ResponseEntity<List<UserListDto>> getAllPersons() {
        return new ResponseEntity<>(personService.getAllPersons(), HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDetailsDto> getPersonById(@PathVariable Long userId) {
        return new ResponseEntity<>(personService.getPersonById(userId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ProcessResult<String>> createPerson(@Valid @RequestBody NewUserDto personDto) {
        return new ResponseEntity<>(personService.createPerson(personDto), HttpStatus.CREATED);
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<ProcessResult<String>> updatePerson(@PathVariable Long userId, @RequestBody UserDetailsDto personDto) {
        return new ResponseEntity<>(personService.updatePerson(userId, personDto), HttpStatus.OK);
    }
}
