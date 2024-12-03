package com.gateway.payload.response;

import java.util.List;

import lombok.Getter;

@Getter
public class PageResponseTrack {

	public PageResponseTrack(List<ResponseTrack> content, PageResponseTrackFromAPI fromAPI) {
		this.content = content;
		pageable = fromAPI.getPageable();
		last = fromAPI.getLast();
		first = fromAPI.getFirst();
		totalPages = fromAPI.getTotalPages();
		totalElements = fromAPI.getTotalElements();
		size = fromAPI.getSize();
		number = fromAPI.getNumber();	
		numberOfElements = fromAPI.getNumberOfElements();
		empty = fromAPI.getEmpty();
	}
	
	List<ResponseTrack> content;
	
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
