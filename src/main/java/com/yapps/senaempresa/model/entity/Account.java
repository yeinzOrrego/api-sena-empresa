package com.yapps.senaempresa.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.yapps.senaempresa.utils.enums.StatusEnum;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "USERS")
public class Account implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, length = 10, unique = true)
    private String userLogin;

    @Column(nullable = false, length = 256)
    private String userPassword;

    @Column(length = 500)
    private String userAddress;

    @Column(nullable = false, length = 10, unique = true)
    private String userIdentification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_type_identification", nullable = false)
    private TypeIdentification typeIdentification;

    @Column(length = 20)
    private String userCellular;

    private Long userCreated;

    @Column(nullable = false, length = 500, unique = true)
    private String userEmail;

    @Column(nullable = false)
    private LocalDateTime dateCreated;

    @Column(nullable = false, length = 500)
    private String userFirstname;

    @Column(nullable = false, length = 500)
    private String userLastname;

    @Column(nullable = false, length = 1)
    private String status;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ApplicationUserRole> userRoles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (userRoles == null)
            return List.of();

        return this.userRoles.stream()
                .filter(role -> role.getRole() != null && StatusEnum.ACTIVO.getValue().equalsIgnoreCase(role.getStatus()))
                .map(role -> {
                    String roleName = role.getRole().getRoleName().toUpperCase();
                    return new SimpleGrantedAuthority("ROLE_" + roleName);
                })
                .toList();
    }

    @Override
    public String getPassword() {
        return this.userPassword;
    }

    @Override
    public String getUsername() {
        return this.userLogin;
    }

}
