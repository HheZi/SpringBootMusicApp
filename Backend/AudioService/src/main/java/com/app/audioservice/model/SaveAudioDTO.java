package com.app.audioservice.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SaveAudioDTO {
	
	private String name;
	
	private byte[] content;
}
