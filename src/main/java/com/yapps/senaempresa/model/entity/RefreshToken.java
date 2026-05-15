package com.yapps.senaempresa.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "REFRESH_TOKEN")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tokenId;

    @Column(nullable = false, unique = true)
    private String refreshToken;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false, length = 1)
    private String status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}