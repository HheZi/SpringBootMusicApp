package com.app.service;


import java.io.File;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.app.payload.RequestImage;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ImageService {

	@Value("${image.dir}")
	private String imageDirName;

	@Value("${image.default}")
	private String defaultImageName;
	
	public Mono<ResponseEntity<FileSystemResource>> getImage(String name){
		return Mono.just(new File(imageDirName, name).getAbsoluteFile())
				.filter(t -> t.exists())
				.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)))
				.map(t -> ResponseEntity.ok()
						.contentType(MediaType.IMAGE_JPEG)
						.body(new FileSystemResource(t)));
	}

	public Mono<ResponseEntity<FileSystemResource>> getDefaultImage() {
		return Mono.just(ResponseEntity.ok()
				.contentType(MediaType.IMAGE_PNG)
				.cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS))
				.body(new FileSystemResource(new File(imageDirName, defaultImageName).getAbsoluteFile())));
	}
	
	public Mono<ResponseEntity<?>> saveImage(RequestImage image) {
		return image.getFile()
				.transferTo(new File(imageDirName, image.getName()).getAbsoluteFile())
				.map(t -> ResponseEntity.status(HttpStatus.CREATED).build());
	}
	
	public Mono<Void> deleteImage(String name) {
		return Mono.just(new File(imageDirName, name).getAbsoluteFile())
				.filter(t -> t.exists() || name.equals(defaultImageName))
				.doOnNext(t -> t.delete())
				.then();
	}
	
}
