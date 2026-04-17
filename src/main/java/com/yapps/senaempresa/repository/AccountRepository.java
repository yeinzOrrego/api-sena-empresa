package com.yapps.senaempresa.repository;

import com.yapps.senaempresa.model.entity.Account;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    Optional<Account> findByUserLogin(String username);

    @EntityGraph(attributePaths = {"userRoles", "userRoles.role"})
    Optional<Account> findWithRolesByUserLoginAndStatus(String username, String status);

    @EntityGraph(attributePaths = {"userRoles", "userRoles.role"})
    Optional<Account> findWithRolesByUserId(Long userId);

    Page<Account> findAll(Specification<Account> specification, Pageable pageable);

    boolean existsByUserIdentification(String userIdentification);

    boolean existsByUserLogin(String userLogin);

    boolean existsByUserEmail(String userEmail);

    boolean existsByUserEmailAndUserIdNot(String userEmail, Long userId);

    boolean existsByUserLoginAndUserIdNot(String userLogin, Long userId);
}
