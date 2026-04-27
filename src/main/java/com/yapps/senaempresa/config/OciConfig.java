package com.yapps.senaempresa.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorageClient;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class OciConfig {

    private final OciProperties props;

    @Bean
    public AuthenticationDetailsProvider authenticationDetailsProvider() throws IOException {
        return new ConfigFileAuthenticationDetailsProvider(props.getConfigFile(), "DEFAULT");
    }

    @Bean
    public ObjectStorageClient objectStorageClient(AuthenticationDetailsProvider auth) {
        return ObjectStorageClient.builder()
                .build(auth);
    }
}