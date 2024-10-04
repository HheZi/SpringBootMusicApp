package com.app.audioservice.controller;

import java.io.InputStream;
import java.lang.module.ModuleDescriptor.Builder;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
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

import lombok.SneakyThrows;

@RestController
@RequestMapping("/api/audio")
public class AudioController {

	@Autowired
	private AudioService audioService;
	
	@GetMapping(value = "/{filename}", produces = "audio/mp3")
	@CrossOrigin
	public ResponseEntity<byte[]> getAudio(@PathVariable("filename") String filename, 
			@RequestHeader(value = "Range", required = false) String rangeHeader){
		if (rangeHeader == null) {
			return ResponseEntity
					.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
					.build();
		}
		return ResponseEntity
				.status(HttpStatus.PARTIAL_CONTENT)
				.header(HttpHeaders.RANGE, rangeHeader)
				.body(audioService.getResource(filename, rangeHeader));
	}
	
}
