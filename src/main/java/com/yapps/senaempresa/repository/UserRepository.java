package com.yapps.senaempresa.repository;

import com.yapps.senaempresa.model.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUserLogin(String username);

    @EntityGraph(attributePaths = {"userRoles", "userRoles.role"})
    Optional<User> findWithRolesByUserLoginAndStatus(String username, String status);

    @EntityGraph(attributePaths = {"userRoles", "userRoles.role"})
    Optional<User> findWithRolesByUserId(Long userId);

    Page<User> findAll(Specification<User> specification, Pageable pageable);

    boolean existsByUserIdentification(String userIdentification);

    boolean existsByUserLogin(String userLogin);

    boolean existsByUserEmail(String userEmail);

    boolean existsByUserEmailAndUserIdNot(String userEmail, Long userId);

    boolean existsByUserLoginAndUserIdNot(String userLogin, Long userId);
}
