package com.app.audioservice.controller;

import java.lang.module.ModuleDescriptor.Builder;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
	@SneakyThrows
	public ResponseEntity<Resource> getAudio(@PathVariable("filename") String filename, 
			@RequestHeader(value =  "Range",  required = false) String rangeHeader){
		List<HttpRange> ranges = HttpRange.parseRanges(rangeHeader);
		Resource resource = audioService.getResource(filename);
		
		if (!ranges.isEmpty()) {
			HttpRange httpRange = ranges.get(0);
			return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
					.header(HttpHeaders.CONTENT_RANGE, String.format("bytes %d-%d/%d", 
							httpRange.getRangeStart(resource.contentLength()), 
							httpRange.getRangeEnd(resource.contentLength()), resource.contentLength()))
					.body(resource);
		}
		return ResponseEntity.ok(resource);
	}
	
}
