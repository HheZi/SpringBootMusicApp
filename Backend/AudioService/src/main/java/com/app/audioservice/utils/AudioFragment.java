package com.app.audioservice.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AudioFragment {

	private byte[] content;
	
	private String rangeHeader;
	
}
