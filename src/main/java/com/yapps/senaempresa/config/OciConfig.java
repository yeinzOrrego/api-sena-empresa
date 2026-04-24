package com.yapps.senaempresa.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorageClient;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class OciConfig {

    private final OciProperties props;

    @Bean
    public AuthenticationDetailsProvider authenticationDetailsProvider() throws IOException {
        String privateKey = new String(Files.readAllBytes(Paths.get(props.getPrivateKeyPath())), StandardCharsets.UTF_8);

        return SimpleAuthenticationDetailsProvider.builder()
                .tenantId(props.getTenancyId())
                .userId(props.getUserId())
                .fingerprint(props.getFingerprint())
                .region(Region.fromRegionId(props.getRegion()))
                .privateKeySupplier(() -> new ByteArrayInputStream(privateKey.getBytes()))
                .build();
    }

    @Bean
    public ObjectStorageClient objectStorageClient(AuthenticationDetailsProvider auth) {
        return ObjectStorageClient.builder()
                .build(auth);
    }
}