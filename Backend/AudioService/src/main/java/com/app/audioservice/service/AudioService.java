package com.app.audioservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

@Service
public class AudioService {

	@Autowired
	private ResourceLoader resourceLoader;
	
	public Resource getResource(String filename) {
		return resourceLoader.getResource(String.format("classpath:/audio/%s.mp3", filename));
	}
	
}
