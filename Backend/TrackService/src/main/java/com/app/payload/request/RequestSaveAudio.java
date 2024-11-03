package com.app.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class RequestSaveAudio {
	private String name;
	
	private byte[] content;
}
