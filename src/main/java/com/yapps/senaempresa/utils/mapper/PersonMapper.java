package com.yapps.senaempresa.utils.mapper;

import com.ada.ecosystem.core.v1.pageable.PageDto;
import com.yapps.senaempresa.config.GlobalMapperConfig;
import com.yapps.senaempresa.model.dto.NewUserDto;
import com.yapps.senaempresa.model.dto.UserDetailsDto;
import com.yapps.senaempresa.model.dto.UserListDto;
import com.yapps.senaempresa.model.entity.Account;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

@Mapper(config = GlobalMapperConfig.class)
public interface PersonMapper {

    @Mapping(target = "userRoles", expression = "java(entity.getUserRoles().stream().map(role -> role.getRole().getRoleId()).toList())")
    UserDetailsDto toDetailsDto(Account entity);

    UserListDto toDto(Account entity);

    PageDto<UserListDto> toPageDto(Page<Account> entities);

    @Mapping(target = "userLogin", source = "userIdentification")
    Account toEntity(NewUserDto dto);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "userRoles", ignore = true)
    void updateEntity(@MappingTarget Account entity, UserDetailsDto dto);

}
