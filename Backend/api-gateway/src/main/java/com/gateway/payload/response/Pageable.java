package com.gateway.payload.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor 
public class Pageable {
	
	private Integer pageNumber;
	
	private Integer pageSize;
	
	private Integer offset;
}