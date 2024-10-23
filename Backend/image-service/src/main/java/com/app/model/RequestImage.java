package com.app.model;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RequestImage {
	
	private String name;
	
	private MultipartFile content;
	
}
