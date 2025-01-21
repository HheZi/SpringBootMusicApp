package com.app.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.payload.RequestImage;
import com.app.service.ImageService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/api/images/")
@RequiredArgsConstructor
public class ImageController {

	private final ImageService imageService;

	@GetMapping("/{name}")
	public Mono<ResponseEntity<Resource>> getImage(@PathVariable("name") String name) {
		return imageService.getImage(name);
	}

	@GetMapping("/default")
	public Mono<ResponseEntity<Resource>> getDefaultCover() {
		return imageService.getDefaultImage();
	}
	
	@PostMapping
	public Mono<Void> saveImage(@ModelAttribute RequestImage image) {
		return imageService.saveImage(image);
	}
	
}
