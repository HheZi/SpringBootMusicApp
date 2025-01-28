package com.app.service;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.app.payload.RequestImage;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ImageService {

	@Value("${image.dir}")
	private String imageDirName;

	@Value("${image.default}")
	private String defaultImageName;
	
	public Mono<ResponseEntity<Resource>> getImage(String name){
		return Mono.just(Paths.get(imageDirName, name))
				.filter(Files::exists)
				.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)))
				.map(t -> ResponseEntity.ok()
						.contentType(MediaType.IMAGE_JPEG)
						.body(new FileSystemResource(t)));
	}

	public Mono<ResponseEntity<Resource>> getDefaultImage() {
		return Mono.just(ResponseEntity.ok()
				.contentType(MediaType.IMAGE_PNG)
				.cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS))
				.body(new FileSystemResource(Paths.get(imageDirName, defaultImageName))));
	}
	
	public Mono<Void> saveImage(RequestImage image) {
		return image.getFile()
				.transferTo(Paths.get(imageDirName, image.getName()));
	}
	
	public Mono<Boolean> deleteImage(String name) {
		return Mono.just(Paths.get(imageDirName, name))
				.filter(t -> !name.equals(defaultImageName))
				.flatMap(this::deleteImage);
	}
	
	@SneakyThrows
	private Mono<Boolean> deleteImage(Path path) {
		return Mono.just(Files.deleteIfExists(path));
	}
	
}
