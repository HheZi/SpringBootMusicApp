package com.app.controller;

import java.util.concurrent.TimeUnit;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.model.RequestImage;
import com.app.service.ImageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/images/")
@RequiredArgsConstructor
public class ImageController {

	private final ImageService imageService;
	
	@GetMapping("/{name}")
	public byte[] getImage(@PathVariable("name") String name) {
		return imageService.getImage(name);
	}
	
	public ResponseEntity<?> saveImage(@RequestBody RequestImage image){
		imageService.saveImage(image);
		return ResponseEntity.status(HttpStatus.CREATED)
				.cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS))
				.build();
	}
	
}
