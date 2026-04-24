package com.yapps.senaempresa.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@ConfigurationProperties(prefix = "oci")
@Component
@Data
public class OciProperties {
    private String tenancyId;
    private String userId;
    private String fingerprint;
    private String region;
    private String privateKeyPath;
    private String bucketName;
    private String namespace;
}
