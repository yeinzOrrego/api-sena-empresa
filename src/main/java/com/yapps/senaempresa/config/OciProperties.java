package com.yapps.senaempresa.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@ConfigurationProperties(prefix = "oci")
@Component
@Data
public class OciProperties {
    private String bucketName;
    private String namespace;
    private String configFile;
}
