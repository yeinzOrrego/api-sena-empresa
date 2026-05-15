package com.yapps.senaempresa.utils.mapper;

import com.ada.ecosystem.core.v1.pageable.PageDto;
import com.yapps.senaempresa.config.GlobalMapperConfig;
import com.yapps.senaempresa.model.dto.NewUserDto;
import com.yapps.senaempresa.model.dto.UserDetailsDto;
import com.yapps.senaempresa.model.dto.UserListDto;
import com.yapps.senaempresa.model.entity.User;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

@Mapper(config = GlobalMapperConfig.class)
public interface PersonMapper {

    @Mapping(target = "userRoles", expression = "java(entity.getUserRoles().stream().filter(userRole -> \"A\".equals(userRole.getStatus())).map(userRole -> userRole.getRole().getRoleId()).toList())")
    @Mapping(target = "userTypeIdentification", source = "typeIdentification.typeId")
    UserDetailsDto toDetailsDto(User entity);

    UserListDto toDto(User entity);

    PageDto<UserListDto> toPageDto(Page<User> entities);

    @Mapping(target = "dateCreated", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "userPassword", ignore = true)
    @Mapping(target = "typeIdentification.typeId", source = "userTypeIdentification")
    @Mapping(target = "userLogin", source = "userIdentification")
    User toEntity(NewUserDto dto);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "typeIdentification", ignore = true)
    @Mapping(target = "userRoles", ignore = true)
    void updateEntity(@MappingTarget User entity, UserDetailsDto dto);

}
