package com.user.payload.response;

import com.user.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ValidatedUser {

	private Integer id;

	private String username;

	private UserRole userRole;

}
