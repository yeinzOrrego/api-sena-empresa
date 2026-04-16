package com.yapps.senaempresa.config;

import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;

/**
 * Global configuration for MapStruct mappers.
 * All mappers should include: {@code @Mapper(config = GlobalMapperConfig.class)}
 */
@MapperConfig(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface GlobalMapperConfig {
}
