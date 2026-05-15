package com.yapps.senaempresa.repository;

import com.yapps.senaempresa.model.entity.User;
import com.yapps.senaempresa.model.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByRefreshTokenAndStatus(String token, String status);

    List<RefreshToken> findAllByUserAndStatus(User user, String status);
}