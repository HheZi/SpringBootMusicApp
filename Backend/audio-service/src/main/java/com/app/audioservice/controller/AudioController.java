package com.app.audioservice.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.audioservice.payload.SaveAudioDTO;
import com.app.audioservice.service.AudioService;


@RestController
@RequestMapping("/api/audio")
public class AudioController {

	@Autowired
	private AudioService audioService;
	
	@GetMapping("/{filename}")
	public ResponseEntity<ResourceRegion> getAudio(
			@PathVariable("filename") String filename, 
			@RequestHeader(value = "Range", required = false) String rangeHeader
		) throws IOException{
		
		if (rangeHeader == null) {
			return ResponseEntity
					.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
					.build();
		}
		
		
		return ResponseEntity
				.status(HttpStatus.PARTIAL_CONTENT)
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.cacheControl(CacheControl.noStore())
				.body(audioService.getResource(filename, rangeHeader));
	}
	
	@PostMapping
	public ResponseEntity<?> saveAudio(@RequestBody SaveAudioDTO dto) {
		
		audioService.saveAudio(dto);
		
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
	@DeleteMapping("/{name}")
	public ResponseEntity<?> deleteAudio(@PathVariable("name") String name){
		audioService.deleteAudio(name);
		return ResponseEntity.ok().build();
	}
}
