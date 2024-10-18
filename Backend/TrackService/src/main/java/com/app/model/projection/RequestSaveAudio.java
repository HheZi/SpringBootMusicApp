package com.app.model.projection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
public class RequestSaveAudio {
	private String name;
	
	private byte[] content;
}
