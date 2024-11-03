package com.auth.model;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

	public RefreshToken(Integer userId) {
		this.userId = userId;
	}
	
}
