package com.app.audioservice.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class SaveAudioDTO {
	
	private String name;
	
	private byte[] content;
}
