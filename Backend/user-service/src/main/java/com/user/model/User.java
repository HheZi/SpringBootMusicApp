package com.user.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table("users")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class User {

	@Id
	private Integer id;

	private String username;

	private String email;

	private String password;

}
