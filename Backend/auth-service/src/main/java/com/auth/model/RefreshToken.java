package com.auth.model;

import com.auth.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table(name = "refresh_tokens")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshToken {

	@Id
	private Integer id;
	
	private UUID token;
	
	@Column("expiration_date")
	private Instant expirationDate;

	@Column("user_id")
	private Integer userId;

	private UserRole userRole;

	public RefreshToken(Integer userId, UserRole userRole) {
		this.userId = userId;
		this.userRole = userRole;
	}
	
}
