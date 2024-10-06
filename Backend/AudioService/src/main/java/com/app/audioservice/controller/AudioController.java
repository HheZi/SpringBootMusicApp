package com.app.audioservice.controller;

import java.io.IOException;
import java.io.InputStream;
import java.lang.module.ModuleDescriptor.Builder;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.audioservice.service.AudioService;
import com.app.audioservice.utils.AudioFragment;

import lombok.SneakyThrows;

@RestController
@RequestMapping("/api/audio")
public class AudioController {

	@Autowired
	private AudioService audioService;
	
	@GetMapping(value = "/{filename}", produces = "audio/mpeg")
	@CrossOrigin(origins = "http://localhost:4200/")
	public ResponseEntity<byte[]> getAudio(@PathVariable("filename") String filename, 
			@RequestHeader(value = "Range", required = false) String rangeHeader) throws IOException{
		if (rangeHeader == null) {
			return ResponseEntity
					.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
					.build();
		}
		AudioFragment resource = audioService.getResource(filename, rangeHeader);
		
		return ResponseEntity
				.status(HttpStatus.PARTIAL_CONTENT)
				.cacheControl(CacheControl.noStore())
				.header(HttpHeaders.CONTENT_RANGE, resource.getRangeHeader())
				.header(HttpHeaders.CONTENT_LENGTH, resource.getContent().length + "")
				.body(resource.getContent());
	}
	
}
