package com.gateway.payload.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PageResponseTrackFromAPI {

	List<ResponseTrackFromAPI> content;
	
	private Pageable pageable;
	
	private Boolean last;

	private Boolean first;
	
	private Integer totalPages;
	
	private Integer totalElements;
	
	private Integer size;
	
	private Integer number;
	
	private Integer numberOfElements;
	
	private Boolean empty;
	
	
}
