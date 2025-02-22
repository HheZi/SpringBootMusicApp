package com.app.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorResponse {

	private Integer id;
	
	private String name;
	
	private String imageUrl;
	
	@JsonInclude(value = Include.NON_NULL)
	private String description;
}
