package com.gateway.payload.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
