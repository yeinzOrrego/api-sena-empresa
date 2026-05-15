package com.yapps.senaempresa.model.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "APPLICATION_USER_ROLE")
@EntityListeners(AuditingEntityListener.class) 
public class ApplicationUserRole {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private Long appUserRoleId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "role_id", nullable = false)
	private Role role;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(nullable = false, length = 1)
	private String status;

	private LocalDateTime dateCreated;

	@CreatedBy
    @Column(nullable = false, updatable = false)
	private Long userCreated;

}
