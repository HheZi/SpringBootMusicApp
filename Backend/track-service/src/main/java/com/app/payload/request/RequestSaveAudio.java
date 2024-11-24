package com.app.payload.request;

import org.springframework.core.io.Resource;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class RequestSaveAudio {
	private String name;
	
	private Resource content;
}
