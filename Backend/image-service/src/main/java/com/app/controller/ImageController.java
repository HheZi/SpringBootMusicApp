package com.app.controller;

import java.util.concurrent.TimeUnit;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.payload.RequestImage;
import com.app.service.ImageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/images/")
@RequiredArgsConstructor
public class ImageController {

	private final ImageService imageService;

	@GetMapping("/{name}")
	public ResponseEntity<byte[]> getImage(@PathVariable("name") String name) {
		return ResponseEntity.status(HttpStatus.OK)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE)
				.body(imageService.getImage(name));
	}

	@GetMapping("/default")
	public ResponseEntity<byte[]> getDefaultCover() {
		return ResponseEntity.ok()
				.cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS))
				.body(imageService.getDefaultImage());
	}
	
	
	@PostMapping
	public ResponseEntity<?> saveImage(@RequestBody RequestImage image) {
		imageService.saveImage(image);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

}
