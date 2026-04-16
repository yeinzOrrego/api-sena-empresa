package com.yapps.senaempresa.repository;

import com.yapps.senaempresa.model.entity.Account;
import com.yapps.senaempresa.model.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByRefreshToken(String token);

    List<RefreshToken> findAllByUserAndStatus(Account user, String status);
}